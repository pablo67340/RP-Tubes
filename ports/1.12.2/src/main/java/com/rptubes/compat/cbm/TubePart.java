package com.rptubes.compat.cbm;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.ISidedHollowConnect;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.IModelRenderPart;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TDynamicRenderPart;
import codechicken.multipart.TIconHitEffectsPart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TNormalOcclusionPart;
import codechicken.multipart.TSlottedPart;
import com.rptubes.block.PneumaticTubeBlock;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A tube in the center slot of a Forge Multipart block space. The tube's behaviour is not
 * reimplemented: the part hosts an ordinary tube tile entity that is never placed in the world,
 * and TubeAccess hands that out wherever the network looks for a tube at this position.
 *
 * The Scala traits here are plain interfaces to javac, so every trait method with a body that
 * TMultiPart also defines (occlusionTest, the hit effects) has to be overridden explicitly.
 */
public class TubePart extends TMultiPart implements TSlottedPart, TNormalOcclusionPart, ITickable,
        IModelRenderPart, TIconHitEffectsPart, TDynamicRenderPart, ISidedHollowConnect {
    private static final int CENTER_SLOT = 6;
    private static final Cuboid6 CORE = box(4, 4, 4, 12, 12, 12);
    /** Indexed by EnumFacing index. */
    private static final Cuboid6[] ARMS = {
            box(4, 0, 4, 12, 4, 12),
            box(4, 12, 4, 12, 16, 12),
            box(4, 4, 0, 12, 12, 4),
            box(4, 4, 12, 12, 12, 16),
            box(0, 4, 4, 4, 12, 12),
            box(12, 4, 4, 16, 12, 12),
    };
    private static final ArmProbe[] ARM_PROBES = new ArmProbe[6];

    static {
        for (int side = 0; side < 6; side++) {
            ARM_PROBES[side] = new ArmProbe(ARMS[side]);
        }
    }

    private static Cuboid6 box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new Cuboid6(x1 / 16, y1 / 16, z1 / 16, x2 / 16, y2 / 16, z2 / 16);
    }

    protected final TubeParts.Kind kind;
    private PneumaticTubeBlockEntity tube;
    /** Tube data read before the part had a world position to build its tile entity at. */
    private NBTTagCompound pending;
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
    public ResourceLocation getType() {
        return this.kind.id;
    }

    public PneumaticTubeBlockEntity tube() {
        if (this.tube == null) {
            IBlockState state = this.kind.block.get().getDefaultState();
            this.tube = (PneumaticTubeBlockEntity) state.getBlock().createTileEntity(this.world(), state);
            this.tube.setWorld(this.world());
            this.tube.readFromNBT(this.pending != null ? this.pending : new NBTTagCompound());
            this.pending = null;
            // readFromNBT also reads the position from the tag, so it has to come after
            this.tube.setPos(this.pos());
            this.tube.setBlockedSides(this.blockedSides);
        }
        return this.tube;
    }

    PneumaticTubeBlockEntity tubeOrNull() {
        return this.tile() != null && this.world() != null ? this.tube() : null;
    }

    void convertFrom(PneumaticTubeBlockEntity source, int connections) {
        this.pending = source.writeToNBT(new NBTTagCompound());
        this.convertedFrom = source;
        this.connections = connections;
    }

    void placedFrom(ItemStack stack) {
        this.pending = new NBTTagCompound();
        this.pending.setByte("pCol", stack.hasTagCompound() ? stack.getTagCompound().getByte("pCol") : 0);
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
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singletonList(CORE);
    }

    @Override
    public boolean occlusionTest(TMultiPart npart) {
        return NormalOcclusionTest.apply(this, npart);
    }

    private List<Cuboid6> boxes() {
        List<Cuboid6> boxes = new ArrayList<>();
        boxes.add(CORE);
        for (int side = 0; side < 6; side++) {
            if ((this.connections & (1 << side)) != 0) {
                boxes.add(ARMS[side]);
            }
        }
        return boxes;
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes() {
        return this.boxes();
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        List<IndexedCuboid6> parts = new ArrayList<>();
        for (Cuboid6 box : this.boxes()) {
            parts.add(new IndexedCuboid6(0, box));
        }
        return parts;
    }

    @Override
    public Cuboid6 getBounds() {
        Cuboid6 bounds = CORE.copy();
        for (Cuboid6 box : this.boxes()) {
            bounds.enclose(box);
        }
        return bounds;
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

    /**
     * Stand-in for NormallyOccludedPart: a Java subclass picks up TMultiPart's always-true
     * occlusionTest rather than the trait's, so it has to be stated here.
     */
    private static final class ArmProbe extends TMultiPart implements TNormalOcclusionPart {
        private final Cuboid6 box;

        ArmProbe(Cuboid6 box) {
            this.box = box;
        }

        @Override
        public ResourceLocation getType() {
            return null;
        }

        @Override
        public Iterable<Cuboid6> getOcclusionBoxes() {
            return Collections.singletonList(this.box);
        }

        @Override
        public boolean occlusionTest(TMultiPart npart) {
            return NormalOcclusionTest.apply(this, npart);
        }
    }

    // ---- state

    /** Server side: re-read covers and neighbours, and tell the client if the visible tube changed. */
    protected void refresh() {
        if (this.world().isRemote) {
            return;
        }
        int blocked = this.computeBlockedSides();
        boolean blockedChanged = blocked != this.blockedSides;
        this.blockedSides = blocked;
        this.tube().setBlockedSides(blocked);
        int conns = TubeLib.getConnections(this.world(), this.pos());
        if (conns != this.connections) {
            this.connections = conns;
            this.sendState();
        }
        if (blockedChanged) {
            this.tile().notifyTileChange();
        }
    }

    protected int glow() {
        return 0;
    }

    protected void setGlow(int glow) {
    }

    protected void sendState() {
        this.getWriteStream().writeByte(this.connections).writeByte(this.tube().getPaintColor()).writeByte(this.glow());
    }

    @Override
    public void read(MCDataInput packet) {
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
    public void onNeighborBlockChanged(BlockPos from) {
        this.refresh();
    }

    @Override
    public void onPartChanged(TMultiPart part) {
        this.refresh();
    }

    @Override
    public void update() {
        if (this.refreshOnTick) {
            this.refreshOnTick = false;
            this.refresh();
        }
        PneumaticTubeBlockEntity tube = this.tube();
        PneumaticTubeBlockEntity.tick(this.world(), this.pos(), null, tube);
    }

    // ---- persistence and sync

    /** The tube's data, without building the tile entity: new parts are described before they have a tile. */
    private NBTTagCompound tubeData() {
        return this.tube != null ? this.tube.writeToNBT(new NBTTagCompound()) : this.pending != null ? this.pending : new NBTTagCompound();
    }

    @Override
    public void save(NBTTagCompound tag) {
        tag.setTag("tube", this.tubeData());
        tag.setByte("conn", (byte) this.connections);
        tag.setByte("blocked", (byte) this.blockedSides);
        tag.setByte("glow", (byte) this.glow());
    }

    @Override
    public void load(NBTTagCompound tag) {
        this.pending = tag.getCompoundTag("tube");
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
        packet.writeNBTTagCompound(this.tubeData());
    }

    @Override
    public void readDesc(MCDataInput packet) {
        this.connections = packet.readUByte() & 0x3F;
        this.setGlow(packet.readUByte());
        this.pending = packet.readNBTTagCompound();
        this.tube = null;
    }

    // ---- interaction, drops

    @Override
    public boolean activate(EntityPlayer player, CuboidRayTraceResult hit, ItemStack held, EnumHand hand) {
        int paint;
        if (held.isEmpty() && player.isSneaking()) {
            paint = 0;
        } else {
            paint = DyeHelper.paintFrom(held);
            if (paint < 0) {
                return false;
            }
        }
        if (!this.world().isRemote && this.tube().tryPaint(paint)) {
            if (paint > 0 && !player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            this.refresh();
            this.sendState();
        }
        return true;
    }

    @Override
    public Iterable<ItemStack> getDrops() {
        return Collections.singletonList(this.stack());
    }

    @Override
    public ItemStack pickItem(CuboidRayTraceResult hit) {
        return this.stack();
    }

    private ItemStack stack() {
        ItemStack stack = new ItemStack(this.kind.block.get());
        byte paint = this.tube != null ? this.tube.getPaintColor() : this.pending != null ? this.pending.getByte("pCol") : 0;
        if (paint > 0) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("pCol", paint);
            stack.setTagCompound(tag);
        }
        return stack;
    }

    @Override
    public float getStrength(EntityPlayer player, CuboidRayTraceResult hit) {
        return this.kind.block.get().getDefaultState().getPlayerRelativeBlockHardness(player, this.world(), this.pos());
    }

    @Override
    public SoundType getPlacementSound(ItemStack stack) {
        return this.kind.block.get().getSoundType();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (this.world().isRemote || this.tube == null) {
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

    // ---- rendering: the block's own model for the tube, its tile renderer for items and paint

    @Override
    public ResourceLocation getModelPath() {
        return this.kind.block.get().getRegistryName();
    }

    @Override
    public BlockStateContainer createBlockStateContainer() {
        return this.kind.block.get().getBlockState();
    }

    @Override
    public IBlockState getCurrentState(IBlockState state) {
        for (EnumFacing dir : EnumFacing.values()) {
            PropertyBool prop = PneumaticTubeBlock.propertyFor(dir);
            state = state.withProperty(prop, (this.connections & (1 << dir.getIndex())) != 0);
        }
        return state;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state) {
        return state;
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer) {
        Block block = this.kind.block.get();
        return block.canRenderInLayer(block.getDefaultState(), layer);
    }

    @Override
    public boolean canRenderDynamic(int pass) {
        return pass == 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 pos, int pass, float frameDelta) {
        PneumaticTubeBlockEntity tube = this.tubeOrNull();
        if (tube == null) {
            return;
        }
        TileEntitySpecialRenderer<PneumaticTubeBlockEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tube);
        if (renderer != null) {
            renderer.render(tube, pos.x, pos.y, pos.z, frameDelta, -1, 1f);
        }
    }

    @Override
    public Cuboid6 getRenderBounds() {
        return Cuboid6.full;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getBreakingIcon(CuboidRayTraceResult hit) {
        return this.getBrokenIcon(hit.sideHit.getIndex());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getBrokenIcon(int side) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes()
                .getTexture(this.getCurrentState(this.kind.block.get().getDefaultState()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addHitEffects(CuboidRayTraceResult hit, ParticleManager manager) {
        IconHitEffects.addHitEffects(this, hit, manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDestroyEffects(CuboidRayTraceResult hit, ParticleManager manager) {
        IconHitEffects.addDestroyEffects(this, manager);
    }
}
