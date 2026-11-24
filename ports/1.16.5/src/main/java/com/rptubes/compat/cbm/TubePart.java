package com.rptubes.compat.cbm;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.microblock.api.ISidedHollowConnect;
import codechicken.multipart.api.MultiPartType;
import codechicken.multipart.api.NormalOcclusionTest;
import codechicken.multipart.api.part.IconHitEffects;
import codechicken.multipart.api.part.IModelRenderPart;
import codechicken.multipart.api.part.ITickablePart;
import codechicken.multipart.api.part.TIconHitEffectsPart;
import codechicken.multipart.api.part.TMultiPart;
import codechicken.multipart.api.part.TNormalOcclusionPart;
import codechicken.multipart.api.part.TSlottedPart;
import codechicken.multipart.util.PartRayTraceResult;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A tube in the center slot of a CB Multipart block space. The tube's behaviour is not
 * reimplemented: the part hosts an ordinary tube tile entity that is never placed in the world,
 * and TubeAccess hands that out wherever the network looks for a tube at this position.
 */
public class TubePart extends TMultiPart implements TSlottedPart, TNormalOcclusionPart, ITickablePart,
        IModelRenderPart, TIconHitEffectsPart, ISidedHollowConnect {
    private static final int CENTER_SLOT = 6;
    private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
    /** Indexed by Direction ordinal. */
    private static final VoxelShape[] ARMS = {
            Block.box(4, 0, 4, 12, 4, 12),
            Block.box(4, 12, 4, 12, 16, 12),
            Block.box(4, 4, 0, 12, 12, 4),
            Block.box(4, 4, 12, 12, 12, 16),
            Block.box(0, 4, 4, 4, 12, 12),
            Block.box(12, 4, 4, 16, 12, 12),
    };
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    static {
        for (int mask = 0; mask < 64; mask++) {
            VoxelShape shape = CORE;
            for (int side = 0; side < 6; side++) {
                if ((mask & (1 << side)) != 0) {
                    shape = VoxelShapes.or(shape, ARMS[side]);
                }
            }
            SHAPES[mask] = shape.optimize();
        }
    }

    protected final TubeParts.Kind kind;
    private PneumaticTubeBlockEntity tube;
    /** Tube data read before the part had a world position to build its tile entity at. */
    private CompoundNBT pending;
    /** The in-world tube this part was converted from, emptied once the conversion goes through. */
    private PneumaticTubeBlockEntity convertedFrom;
    private int connections;
    private int blockedSides;
    /** Loaded parts get no onAdded, so they re-read covers and neighbours on their first tick. */
    private boolean refreshOnTick;

    public TubePart(TubeParts.Kind kind) {
        this.kind = kind;
    }

    @Override
    public MultiPartType<?> getType() {
        return this.kind.type;
    }

    public PneumaticTubeBlockEntity tube() {
        if (this.tube == null) {
            BlockState state = this.kind.block.get().defaultBlockState();
            this.tube = (PneumaticTubeBlockEntity) state.getBlock().createTileEntity(state, this.world());
            // load also reads the position from the tag, so it has to come before setLevelAndPosition
            this.tube.load(state, this.pending != null ? this.pending : new CompoundNBT());
            this.pending = null;
            this.tube.setLevelAndPosition(this.world(), this.pos());
            this.tube.setBlockedSides(this.blockedSides);
        }
        return this.tube;
    }

    PneumaticTubeBlockEntity tubeOrNull() {
        return this.tile() != null && this.tile().getLevel() != null ? this.tube() : null;
    }

    void convertFrom(PneumaticTubeBlockEntity source, BlockState state) {
        this.pending = source.save(new CompoundNBT());
        this.convertedFrom = source;
        int mask = 0;
        for (Direction dir : Direction.values()) {
            if (state.getValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(dir))) {
                mask |= 1 << dir.ordinal();
            }
        }
        this.connections = mask;
    }

    void placedFrom(ItemStack stack) {
        this.pending = new CompoundNBT();
        this.pending.putByte("pCol", stack.hasTag() ? stack.getTag().getByte("pCol") : 0);
    }

    @Override
    public void invalidateConvertedTile() {
        if (this.convertedFrom != null) {
            // The block is about to be replaced; its contents now live in this part.
            this.convertedFrom.getTubeFlow().contents.clear();
            this.convertedFrom = null;
        }
    }

    // ---- shape and occlusion

    @Override
    public int getSlotMask() {
        return 1 << CENTER_SLOT;
    }

    @Override
    public int getHollowSize(int side) {
        return 8;
    }

    @Override
    public VoxelShape getOcclusionShape() {
        return CORE;
    }

    @Override
    public boolean occlusionTest(TMultiPart npart) {
        return NormalOcclusionTest.test(this, npart) && super.occlusionTest(npart);
    }

    @Override
    public VoxelShape getShape(ISelectionContext context) {
        return SHAPES[this.connections];
    }

    private int computeBlockedSides() {
        int mask = 0;
        for (int side = 0; side < 6; side++) {
            if (!this.tile().canReplacePart(this, ARM_PROBES[side])) {
                mask |= 1 << side;
            }
        }
        return mask;
    }

    private static final ArmProbe[] ARM_PROBES = new ArmProbe[6];

    static {
        for (int side = 0; side < 6; side++) {
            ARM_PROBES[side] = new ArmProbe(ARMS[side]);
        }
    }

    /**
     * Stand-in for NormalOcclusionTest.of, whose Java part inherits TMultiPart's always-true
     * occlusionTest instead of the trait's, so it never collides with a cover.
     */
    private static final class ArmProbe extends TMultiPart implements TNormalOcclusionPart {
        private final VoxelShape shape;

        ArmProbe(VoxelShape shape) {
            this.shape = shape;
        }

        @Override
        public MultiPartType<?> getType() {
            return null;
        }

        @Override
        public VoxelShape getOcclusionShape() {
            return this.shape;
        }

        @Override
        public boolean occlusionTest(TMultiPart npart) {
            return NormalOcclusionTest.test(this, npart) && super.occlusionTest(npart);
        }
    }

    // ---- state

    /** Server side: re-read covers and neighbours, and tell the client if the visible tube changed. */
    protected void refresh() {
        if (this.world().isClientSide) {
            return;
        }
        int blocked = this.computeBlockedSides();
        boolean blockedChanged = blocked != this.blockedSides;
        this.blockedSides = blocked;
        this.tube().setBlockedSides(blocked);
        int conns = TubeLib.getConnections(this.world(), this.pos());
        if (conns != this.connections) {
            this.connections = conns;
            this.tile().markShapeChange();
            this.sendState();
        }
        if (blockedChanged) {
            this.world().updateNeighborsAt(this.pos(), this.tile().getBlockState().getBlock());
        }
    }

    protected int glow() {
        return 0;
    }

    protected void setGlow(int glow) {
    }

    protected void sendState() {
        this.sendUpdate(packet -> {
            packet.writeByte(this.connections);
            packet.writeByte(this.tube().getPaintColor());
            packet.writeByte(this.glow());
        });
    }

    @Override
    public void readUpdate(MCDataInput packet) {
        this.connections = packet.readUByte() & 0x3F;
        this.tube().tryPaint(packet.readUByte());
        this.setGlow(packet.readUByte());
        this.tile().markRender();
    }

    @Override
    public void onAdded() {
        super.onAdded();
        this.refresh();
    }

    @Override
    public void onNeighborBlockChanged(BlockPos neighbor) {
        this.refresh();
    }

    @Override
    public void onPartChanged(TMultiPart part) {
        this.refresh();
    }

    @Override
    public void tick() {
        if (this.refreshOnTick) {
            this.refreshOnTick = false;
            this.refresh();
        }
        PneumaticTubeBlockEntity tube = this.tube();
        PneumaticTubeBlockEntity.tick(this.world(), this.pos(), tube.getBlockState(), tube);
    }

    // ---- persistence and sync

    /** The tube's data, without building the tile entity: new parts are described before they have a tile. */
    private CompoundNBT tubeData() {
        return this.tube != null ? this.tube.save(new CompoundNBT()) : this.pending != null ? this.pending : new CompoundNBT();
    }

    @Override
    public void save(CompoundNBT tag) {
        tag.put("tube", this.tubeData());
        tag.putByte("conn", (byte) this.connections);
        tag.putByte("blocked", (byte) this.blockedSides);
        tag.putByte("glow", (byte) this.glow());
    }

    @Override
    public void load(CompoundNBT tag) {
        this.pending = tag.getCompound("tube");
        this.connections = tag.getByte("conn") & 0x3F;
        this.blockedSides = tag.getByte("blocked") & 0x3F;
        this.setGlow(tag.getByte("glow"));
        this.tube = null;
        this.refreshOnTick = true;
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        packet.writeByte(this.connections);
        packet.writeByte(this.glow());
        packet.writeCompoundNBT(this.tubeData());
    }

    @Override
    public void readDesc(MCDataInput packet) {
        this.connections = packet.readUByte() & 0x3F;
        this.setGlow(packet.readUByte());
        this.pending = packet.readCompoundNBT();
        this.tube = null;
    }

    // ---- interaction, drops

    @Override
    public ActionResultType activate(PlayerEntity player, PartRayTraceResult hit, ItemStack held, Hand hand) {
        int paint;
        if (held.isEmpty() && player.isShiftKeyDown()) {
            paint = 0;
        } else {
            paint = DyeHelper.paintFrom(held);
            if (paint < 0) {
                return ActionResultType.PASS;
            }
        }
        if (!this.world().isClientSide && this.tube().tryPaint(paint)) {
            if (paint > 0 && !player.abilities.instabuild) {
                held.shrink(1);
            }
            this.refresh();
            this.sendState();
        }
        return ActionResultType.sidedSuccess(this.world().isClientSide);
    }

    @Override
    public Iterable<ItemStack> getDrops() {
        return Collections.singletonList(this.stack());
    }

    @Override
    public ItemStack pickItem(PartRayTraceResult hit) {
        return this.stack();
    }

    private ItemStack stack() {
        ItemStack stack = new ItemStack(this.kind.block.get());
        byte paint = this.tube != null ? this.tube.getPaintColor() : this.pending != null ? this.pending.getByte("pCol") : 0;
        if (paint > 0) {
            stack.getOrCreateTag().putByte("pCol", paint);
        }
        return stack;
    }

    @Override
    public float getStrength(PlayerEntity player, PartRayTraceResult hit) {
        return this.kind.block.get().defaultBlockState().getDestroyProgress(player, this.world(), this.pos());
    }

    @Override
    public SoundType getPlacementSound(ItemUseContext context) {
        return this.kind.block.get().defaultBlockState().getSoundType();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (this.world().isClientSide || this.tube == null) {
            return;
        }
        List<ItemStack> drops = new ArrayList<>();
        for (TubeItem ti : this.tube.getTubeFlow().contents) {
            if (ti != null && !ti.item.isEmpty()) {
                drops.add(ti.item.copy());
            }
        }
        this.tube.getTubeFlow().contents.clear();
        if (!drops.isEmpty()) {
            this.tile().dropItems(drops);
        }
    }

    // ---- rendering

    @Override
    public BlockState getCurrentState() {
        BlockState state = this.kind.block.get().defaultBlockState();
        for (Direction dir : Direction.values()) {
            state = state.setValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(dir), (this.connections & (1 << dir.ordinal())) != 0);
        }
        return state;
    }

    @Override
    public boolean canRenderInLayer(RenderType layer) {
        return RenderTypeLookup.canRenderInLayer(this.getCurrentState(), layer);
    }

    @Override
    public IModelData getModelData() {
        return EmptyModelData.INSTANCE;
    }

    @Override
    public Cuboid6 getBounds() {
        return new Cuboid6(this.getShape(ISelectionContext.empty()).bounds());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public TextureAtlasSprite getBreakingIcon(PartRayTraceResult hit) {
        return this.getBrokenIcon(hit.getDirection().ordinal());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public TextureAtlasSprite getBrokenIcon(int side) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(this.getCurrentState())
                .getParticleTexture(EmptyModelData.INSTANCE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addHitEffects(PartRayTraceResult hit, ParticleManager manager) {
        IconHitEffects.addHitEffects(this, hit, manager);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addDestroyEffects(PartRayTraceResult hit, ParticleManager manager) {
        IconHitEffects.addDestroyEffects(this, manager, true);
    }
}
