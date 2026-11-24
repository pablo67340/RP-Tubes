//? if cbm {
package com.rptubes.compat.cbm;

//? if <1.21.11 {
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.api.part.ModelRenderPart;
//?} else {
/*import codechicken.multipart.api.part.BlockStateModelPart;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
*///?}
import codechicken.microblock.api.SlottedHollowConnect;
import codechicken.multipart.api.MultipartType;
import codechicken.multipart.api.NormalOcclusionTest;
import codechicken.multipart.api.part.BaseMultipart;
import codechicken.multipart.api.part.NormalOcclusionPart;
import codechicken.multipart.api.part.SlottedPart;
import codechicken.multipart.api.part.TickablePart;
import codechicken.multipart.util.PartRayTraceResult;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

/**
 * A tube in the center slot of a CB Multipart block space. The tube's behaviour is not
 * reimplemented: the part hosts an ordinary tube block entity that is never placed in the world,
 * and TubeAccess hands that out wherever the network looks for a tube at this position.
 */
//? if <1.21.11 {
public class TubePart extends BaseMultipart implements SlottedPart, NormalOcclusionPart, TickablePart, ModelRenderPart, SlottedHollowConnect {
//?} else {
/*public class TubePart extends BaseMultipart implements SlottedPart, NormalOcclusionPart, TickablePart, BlockStateModelPart, SlottedHollowConnect {
*///?}
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
                    shape = Shapes.or(shape, ARMS[side]);
                }
            }
            SHAPES[mask] = shape.optimize();
        }
    }

    protected final TubeParts.Kind kind;
    private PneumaticTubeBlockEntity tube;
    /** Tube data read before the part had a world position to build its block entity at. */
    private CompoundTag pending;
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
    public MultipartType<?> getType() {
        return this.kind.type.get();
    }

    public PneumaticTubeBlockEntity tube() {
        if (this.tube == null) {
            Block block = this.kind.block.get();
            this.tube = (PneumaticTubeBlockEntity) ((EntityBlock) block).newBlockEntity(this.pos(), block.defaultBlockState());
            this.tube.setLevel(this.level());
            if (this.pending != null) {
                TubeData.load(this.tube, this.pending);
                this.pending = null;
            }
            this.tube.setBlockedSides(this.blockedSides);
        }
        return this.tube;
    }

    PneumaticTubeBlockEntity tubeOrNull() {
        return this.hasTile() && this.tile().getLevel() != null ? this.tube() : null;
    }

    void convertFrom(PneumaticTubeBlockEntity source, BlockState state) {
        this.pending = TubeData.save(source);
        this.convertedFrom = source;
        int mask = 0;
        for (Direction dir : Direction.values()) {
            if (state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir))) {
                mask |= 1 << dir.ordinal();
            }
        }
        this.connections = mask;
    }

    void placedFrom(ItemStack stack) {
        this.pending = new CompoundTag();
        this.pending.putByte("pCol", TubeData.readPaint(stack));
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
    public int getHoleSize(int side) {
        return 8;
    }

    @Override
    public VoxelShape getOcclusionShape() {
        return CORE;
    }

    @Override
    public VoxelShape getShape(CollisionContext context) {
        return SHAPES[this.connections];
    }

    private int computeBlockedSides() {
        int mask = 0;
        for (int side = 0; side < 6; side++) {
            if (!this.tile().canReplacePart(this, NormalOcclusionTest.of(ARMS[side]))) {
                mask |= 1 << side;
            }
        }
        return mask;
    }

    // ---- state

    /** Server side: re-read covers and neighbours, and tell the client if the visible tube changed. */
    protected void refresh() {
        if (this.level().isClientSide()) {
            return;
        }
        int blocked = this.computeBlockedSides();
        boolean blockedChanged = blocked != this.blockedSides;
        this.blockedSides = blocked;
        this.tube().setBlockedSides(blocked);
        int conns = TubeLib.getConnections(this.level(), this.pos());
        if (conns != this.connections) {
            this.connections = conns;
            //? if <1.20.5
            //this.tile().markShapeChange();
            this.sendState();
        }
        if (blockedChanged) {
            this.level().updateNeighborsAt(this.pos(), this.tile().getBlockState().getBlock());
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
    //? if <1.21.11 {
    public void readUpdate(MCDataInput packet) {
        this.connections = packet.readUByte() & 0x3F;
        this.tube().tryPaint(packet.readUByte());
        this.setGlow(packet.readUByte());
    //?} else {
    /*public void readUpdate(RegistryFriendlyByteBuf packet) {
        this.connections = packet.readUnsignedByte() & 0x3F;
        this.tube().tryPaint(packet.readUnsignedByte());
        this.setGlow(packet.readUnsignedByte());
    *///?}
        this.tile().markRender();
    }

    @Override
    public void onAdded() {
        super.onAdded();
        this.refresh();
    }

    @Override
    public void onNeighborBlockChanged(net.minecraft.core.BlockPos neighbor) {
        this.refresh();
    }

    @Override
    public void onPartChanged(codechicken.multipart.api.part.MultiPart part) {
        this.refresh();
    }

    @Override
    public void tick() {
        if (this.refreshOnTick) {
            this.refreshOnTick = false;
            this.refresh();
        }
        PneumaticTubeBlockEntity tube = this.tube();
        PneumaticTubeBlockEntity.tick(this.level(), this.pos(), tube.getBlockState(), tube);
    }

    // ---- persistence and sync

    /** The tube's data, without building the block entity: new parts are described before they have a tile. */
    private CompoundTag tubeData() {
        return this.tube != null ? TubeData.save(this.tube) : this.pending != null ? this.pending : new CompoundTag();
    }

    //? if <1.21.11 {
    @Override
    //? if <1.20.5 {
    /*public void save(CompoundTag tag) {
    *///?} else {
    public void save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    //?}
        tag.put("tube", this.tubeData());
        tag.putByte("conn", (byte) this.connections);
        tag.putByte("blocked", (byte) this.blockedSides);
        tag.putByte("glow", (byte) this.glow());
    }

    @Override
    //? if <1.20.5 {
    /*public void load(CompoundTag tag) {
    *///?} else {
    public void load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    //?}
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
    //?} else {
    /*@Override
    public void save(ValueOutput output) {
        output.store("tube", CompoundTag.CODEC, this.tubeData());
        output.putByte("conn", (byte) this.connections);
        output.putByte("blocked", (byte) this.blockedSides);
        output.putByte("glow", (byte) this.glow());
    }

    @Override
    public void load(ValueInput input) {
        this.pending = input.read("tube", CompoundTag.CODEC).orElseGet(CompoundTag::new);
        this.connections = input.getByteOr("conn", (byte) 0) & 0x3F;
        this.blockedSides = input.getByteOr("blocked", (byte) 0) & 0x3F;
        this.setGlow(input.getByteOr("glow", (byte) 0));
        this.tube = null;
        this.refreshOnTick = true;
    }

    @Override
    public void writeDesc(RegistryFriendlyByteBuf packet) {
        packet.writeByte(this.connections);
        packet.writeByte(this.glow());
        packet.writeNbt(this.tubeData());
    }

    @Override
    public void readDesc(RegistryFriendlyByteBuf packet) {
        this.connections = packet.readUnsignedByte() & 0x3F;
        this.setGlow(packet.readUnsignedByte());
        this.pending = packet.readNbt();
        this.tube = null;
    }
    *///?}

    // ---- interaction, drops

    //? if <1.20.5 {
    /*@Override
    public InteractionResult activate(Player player, PartRayTraceResult hit, ItemStack held, InteractionHand hand) {
        if (held.isEmpty() && player.isShiftKeyDown()) {
            return this.paint(player, held, 0);
        }
        int paint = DyeHelper.paintFrom(held);
        return paint < 0 ? InteractionResult.PASS : this.paint(player, held, paint);
    }
    *///?} else if <1.21.11 {
    @Override
    public net.minecraft.world.ItemInteractionResult useItemOn(ItemStack held, Player player, PartRayTraceResult hit, InteractionHand hand) {
        int paint = DyeHelper.paintFrom(held);
        if (paint < 0) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        this.paint(player, held, paint);
        return net.minecraft.world.ItemInteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    public InteractionResult useWithoutItem(Player player, PartRayTraceResult hit) {
        return player.isShiftKeyDown() ? this.paint(player, ItemStack.EMPTY, 0) : InteractionResult.PASS;
    }
    //?} else {
    /*@Override
    public InteractionResult useItemOn(ItemStack held, Player player, PartRayTraceResult hit, InteractionHand hand) {
        int paint = DyeHelper.paintFrom(held);
        return paint < 0 ? InteractionResult.TRY_WITH_EMPTY_HAND : this.paint(player, held, paint);
    }

    @Override
    public InteractionResult useWithoutItem(Player player, PartRayTraceResult hit) {
        return player.isShiftKeyDown() ? this.paint(player, ItemStack.EMPTY, 0) : InteractionResult.PASS;
    }
    *///?}

    private InteractionResult paint(Player player, ItemStack held, int paint) {
        if (!this.level().isClientSide() && this.tube().tryPaint(paint)) {
            if (paint > 0 && !player.getAbilities().instabuild) {
                held.shrink(1);
            }
            this.refresh();
            this.sendState();
        }
        //? if <1.21.11 {
        return InteractionResult.sidedSuccess(this.level().isClientSide());
        //?} else {
        /*return InteractionResult.SUCCESS;
        *///?}
    }

    @Override
    //? if <1.21.11 {
    public Iterable<ItemStack> getDrops() {
    //?} else {
    /*public Iterable<ItemStack> getDrops(LootParams.Builder builder) {
    *///?}
        return List.of(this.stack());
    }

    @Override
    //? if <1.21.11 {
    public ItemStack getCloneStack(PartRayTraceResult hit) {
    //?} else {
    /*public ItemStack getCloneStack(PartRayTraceResult hit, Player player) {
    *///?}
        return this.stack();
    }

    private ItemStack stack() {
        ItemStack stack = new ItemStack(this.kind.block.get());
        TubeData.writePaint(stack, this.tube != null ? this.tube.getPaintColor() : this.pending != null ? TubeData.paint(this.pending) : 0);
        return stack;
    }

    @Override
    public float getStrength(Player player, PartRayTraceResult hit) {
        return this.kind.block.get().defaultBlockState().getDestroyProgress(player, this.level(), this.pos());
    }

    @Override
    //? if <1.21.11 {
    public SoundType getPlacementSound(UseOnContext context) {
    //?} else {
    /*public SoundType getSound(UseOnContext context) {
    *///?}
        return this.kind.block.get().defaultBlockState().getSoundType();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (this.level().isClientSide() || this.tube == null) {
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
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir), (this.connections & (1 << dir.ordinal())) != 0);
        }
        return state;
    }
}
//?}
