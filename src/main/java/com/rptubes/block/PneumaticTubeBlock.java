package com.rptubes.block;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
//? if forge && >=1.21.11 {
/*import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.PaintColors;
*///?} else if forgelike || <1.20.6 {
import com.rptubes.tube.DyeHelper;
//?}
import com.rptubes.tube.TubeLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if <1.20.1 {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
*///?} else if <1.20.6 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
*///?} else if neoforge && <1.21.11 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
//?} else if forgelike {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
*///?}
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
//? if <1.20.1 {
/*import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.LevelAccessor;
*///?} else if forge {
/*import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.LevelAccessor;
*///?} else if <1.20.6 {
/*import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
*///?} else {
import net.minecraft.world.level.LevelAccessor;
//?}
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
//? if neoforge {
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
//?}
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

//? if forgelike || <1.20.6 {
import java.util.List;
//?}
import java.util.Map;
import java.util.function.BiFunction;

/** Thin pneumatic tube. Connection mask mirrors RedPower TubeLib side bits. */
public class PneumaticTubeBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;

    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    // Match OG RenderTube doubleBox bounds: 0.25..0.75 => 4..12
    private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
    private static final VoxelShape ARM_N = Block.box(4, 4, 0, 12, 12, 4);
    private static final VoxelShape ARM_S = Block.box(4, 4, 12, 12, 12, 16);
    private static final VoxelShape ARM_W = Block.box(0, 4, 4, 4, 12, 12);
    private static final VoxelShape ARM_E = Block.box(12, 4, 4, 16, 12, 12);
    private static final VoxelShape ARM_D = Block.box(4, 0, 4, 12, 4, 12);
    private static final VoxelShape ARM_U = Block.box(4, 12, 4, 12, 16, 12);

    private final BiFunction<BlockPos, BlockState, BlockEntity> beFactory;
    //? if <26.1.2 || forgelike {
    private final BlockEntityType.BlockEntitySupplier<? extends PneumaticTubeBlockEntity> tickerRef;
    //?}

    public PneumaticTubeBlock(Properties properties) {
        //? if fabric && >=26.1.2 {
        /*this(properties, PneumaticTubeBlockEntity::new);
        *///?} else {
        this(properties, PneumaticTubeBlockEntity::new, null);
        //?}
    }

    public PneumaticTubeBlock(Properties properties,
                              //? if fabric && >=26.1.2 {
                              /*BiFunction<BlockPos, BlockState, BlockEntity> beFactory) {
                              *///?} else {
                              BiFunction<BlockPos, BlockState, BlockEntity> beFactory,
                              BlockEntityType.BlockEntitySupplier<? extends PneumaticTubeBlockEntity> tickerRef) {
                              //?}
        super(properties);
        this.beFactory = beFactory;
        //? if <26.1.2 || forgelike {
        this.tickerRef = tickerRef;
        //?}
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
                .setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, ARM_N);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, ARM_S);
        if (state.getValue(WEST)) shape = Shapes.or(shape, ARM_W);
        if (state.getValue(EAST)) shape = Shapes.or(shape, ARM_E);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, ARM_D);
        if (state.getValue(UP)) shape = Shapes.or(shape, ARM_U);
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getConnectionState(context.getLevel(), this.defaultBlockState(), context.getClickedPos());
    }

    @Override
    //? if <1.21.11 {
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    //?} else {
    /*protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level,
                                     net.minecraft.world.level.ScheduledTickAccess tickAccess, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState,
                                     net.minecraft.util.RandomSource random) {
    *///?}
        if (level instanceof Level realLevel) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                    TubeLib.canConnect(realLevel, pos, direction));
        }
        return state;
    }

    @Override
    //? if <1.20.1 {
    /*public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && held.isEmpty()) {
            if (!level.isClientSide) {
                tube.tryPaint(0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        int paint = DyeHelper.paintFrom(held);
        if (paint < 0) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube && stack.hasTag()) {
            tube.tryPaint(stack.getTag().getByte("pCol"));
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (ItemStack stack : drops) {
                    stack.getOrCreateTag().putByte("pCol", color);
                }
            }
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                stack.getOrCreateTag().putByte("pCol", color);
            }
        }
        return stack;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (!level.isClientSide) {
    *///?} else if !forge && <1.21.11 {
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (!level.isClientSide) {
    //?} else if <1.20.6 {
    /*public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && held.isEmpty()) {
            if (!level.isClientSide) {
                tube.tryPaint(0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        int paint = DyeHelper.paintFrom(held);
        if (paint < 0) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube && stack.hasTag()) {
            tube.tryPaint(stack.getTag().getByte("pCol"));
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (ItemStack stack : drops) {
                    stack.getOrCreateTag().putByte("pCol", color);
                }
            }
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                stack.getOrCreateTag().putByte("pCol", color);
            }
        }
        return stack;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (!level.isClientSide) {
    *///?} else if <1.21.11 {
    /*protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        int paint = DyeHelper.paintFrom(held);
        if (paint < 0) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                tube.tryPaint(0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            tube.tryPaint(data.copyTag().getByte("pCol"));
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            writePaint(drops, tube.getPaintColor());
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            writePaint(stack, tube.getPaintColor());
        }
        return stack;
    }

    private static void writePaint(List<ItemStack> drops, byte color) {
        if (color <= 0) {
            return;
        }
        for (ItemStack stack : drops) {
            writePaint(stack, color);
        }
    }

    private static void writePaint(ItemStack stack, byte color) {
        if (color <= 0) {
            return;
        }
        CompoundTag tag = new CompoundTag();
        tag.putByte("pCol", color);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (!level.isClientSide) {
    *///?} else if !forge {
    /*public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation,
                                boolean isMoving) {
        if (!level.isClientSide()) {
    *///?} else {
    /*protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        int paint = DyeHelper.paintFrom(held);
        if (paint < 0) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!level.isClientSide() && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube && player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                tube.tryPaint(0);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            tube.tryPaint(data.copyTag().getByteOr("pCol", (byte) 0));
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            writePaint(drops, tube.getPaintColor());
        }
        return drops;
    }

    private static void writePaint(List<ItemStack> drops, byte color) {
        if (color <= 0) {
            return;
        }
        for (ItemStack stack : drops) {
            CompoundTag tag = new CompoundTag();
            tag.putByte("pCol", color);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            stack.set(DataComponents.DYED_COLOR, new net.minecraft.world.item.component.DyedItemColor(PaintColors.rgb(color)));
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation,
                                boolean isMoving) {
        if (!level.isClientSide()) {
    *///?}
            BlockState updated = getConnectionState(level, state, pos);
            if (updated != state) {
                level.setBlock(pos, updated, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            level.setBlock(pos, getConnectionState(level, state, pos), Block.UPDATE_ALL);
        }
    }

    protected BlockState getConnectionState(Level level, BlockState state, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), TubeLib.canConnect(level, pos, dir));
        }
        return state;
    }

    //? if fabric && <1.20.6 {
    /*@Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        return DyeHelper.tryUseOnTube(player, level, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            tube.tryPaint(DyeHelper.readPaint(stack));
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            DyeHelper.writePaint(stack, tube.getPaintColor());
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (ItemStack drop : drops) {
                    DyeHelper.writePaint(drop, color);
                }
            }
        }
        return drops;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        BlockEntityType<? extends PneumaticTubeBlockEntity> expected =
                this.tickerRef == null ? ModBlockEntities.PNEUMATIC_TUBE : null;
        // Resolve lazily after registry is ready via override in subclasses when needed
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE, PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE, PneumaticTubeBlockEntity::tick);
    }
    *///?} else if fabric && >=1.21.11 && <26.1.2 {

    /*@Override
    protected net.minecraft.world.InteractionResult useItemOn(net.minecraft.world.item.ItemStack held, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
        return com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        return com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, player.getUsedItemHand(), hit);
    }

    @Override
    public void setPlacedBy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            tube.tryPaint(com.rptubes.tube.DyeHelper.readPaint(stack));
        }
    }

    @Override
    protected net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, boolean includeData) {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(this);
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            com.rptubes.tube.DyeHelper.writePaint(stack, tube.getPaintColor());
        }
        return stack;
    }

    @Override
    public java.util.List<net.minecraft.world.item.ItemStack> getDrops(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        java.util.List<net.minecraft.world.item.ItemStack> drops = super.getDrops(state, builder);
        net.minecraft.world.level.block.entity.BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (net.minecraft.world.item.ItemStack drop : drops) {
                    com.rptubes.tube.DyeHelper.writePaint(drop, color);
                }
            }
        }
        return drops;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        BlockEntityType<? extends PneumaticTubeBlockEntity> expected =
                this.tickerRef == null ? ModBlockEntities.PNEUMATIC_TUBE : null;
        // Resolve lazily after registry is ready via override in subclasses when needed
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE, PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE, PneumaticTubeBlockEntity::tick);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(PneumaticTubeBlock::new);
    }
    *///?} else if <1.20.6 {
    /*@Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        BlockEntityType<? extends PneumaticTubeBlockEntity> expected =
                this.tickerRef == null ? ModBlockEntities.PNEUMATIC_TUBE.get() : null;
        // Resolve lazily after registry is ready via override in subclasses when needed
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE.get(), PneumaticTubeBlockEntity::tick);
    }
    *///?} else if fabric && <1.21.11 {

    /*@Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack held, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
        net.minecraft.world.InteractionResult r = com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
        if (r.consumesAction()) {
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        return com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, player.getUsedItemHand(), hit);
    }

    @Override
    public void setPlacedBy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            tube.tryPaint(com.rptubes.tube.DyeHelper.readPaint(stack));
        }
    }

    @Override
    public net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(this);
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            com.rptubes.tube.DyeHelper.writePaint(stack, tube.getPaintColor());
        }
        return stack;
    }

    @Override
    public java.util.List<net.minecraft.world.item.ItemStack> getDrops(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        java.util.List<net.minecraft.world.item.ItemStack> drops = super.getDrops(state, builder);
        net.minecraft.world.level.block.entity.BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (net.minecraft.world.item.ItemStack drop : drops) {
                    com.rptubes.tube.DyeHelper.writePaint(drop, color);
                }
            }
        }
        return drops;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        BlockEntityType<? extends PneumaticTubeBlockEntity> expected =
                this.tickerRef == null ? ModBlockEntities.PNEUMATIC_TUBE : null;
        // Resolve lazily after registry is ready via override in subclasses when needed
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE, PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE, PneumaticTubeBlockEntity::tick);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(PneumaticTubeBlock::new);
    }
    *///?} else if neoforge && <1.21.11 {
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        BlockEntityType<? extends PneumaticTubeBlockEntity> expected =
                this.tickerRef == null ? ModBlockEntities.PNEUMATIC_TUBE.get() : null;
        // Resolve lazily after registry is ready via override in subclasses when needed
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE.get(), PneumaticTubeBlockEntity::tick);
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        int paint = DyeHelper.paintFrom(held);
        if (paint < 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (player.isShiftKeyDown() && be instanceof PneumaticTubeBlockEntity tube) {
            if (!level.isClientSide) {
                tube.tryPaint(0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof PneumaticTubeBlockEntity tube) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                tube.tryPaint(data.copyTag().getByte("pCol"));
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (ItemStack drop : drops) {
                    CompoundTag tag = new CompoundTag();
                    CustomData existing = drop.get(DataComponents.CUSTOM_DATA);
                    if (existing != null) {
                        tag = existing.copyTag();
                    }
                    tag.putByte("pCol", color);
                    drop.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                }
            }
        }
        return drops;
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(PneumaticTubeBlock::new);
    }
    //?} else if fabric {

    /*@Override
    protected net.minecraft.world.InteractionResult useItemOn(net.minecraft.world.item.ItemStack held, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
        return com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        return com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, player.getUsedItemHand(), hit);
    }

    @Override
    public void setPlacedBy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            tube.tryPaint(com.rptubes.tube.DyeHelper.readPaint(stack));
        }
    }

    @Override
    protected net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, boolean includeData) {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(this);
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            com.rptubes.tube.DyeHelper.writePaint(stack, tube.getPaintColor());
        }
        return stack;
    }

    @Override
    public java.util.List<net.minecraft.world.item.ItemStack> getDrops(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        java.util.List<net.minecraft.world.item.ItemStack> drops = super.getDrops(state, builder);
        net.minecraft.world.level.block.entity.BlockEntity be = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (net.minecraft.world.item.ItemStack drop : drops) {
                    com.rptubes.tube.DyeHelper.writePaint(drop, color);
                }
            }
        }
        return drops;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR, PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE, PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE, PneumaticTubeBlockEntity::tick);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(PneumaticTubeBlock::new);
    }
    *///?} else if neoforge {
    /*@Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        BlockEntityType<? extends PneumaticTubeBlockEntity> expected =
                this.tickerRef == null ? ModBlockEntities.PNEUMATIC_TUBE.get() : null;
        // Resolve lazily after registry is ready via override in subclasses when needed
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE.get(), PneumaticTubeBlockEntity::tick);
    }


    @Override
    protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return InteractionResult.PASS;
        }
        int paint = DyeHelper.paintFrom(held);
        if (paint < 0) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (player.isShiftKeyDown() && be instanceof PneumaticTubeBlockEntity tube) {
            if (!level.isClientSide()) {
                tube.tryPaint(0);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof PneumaticTubeBlockEntity tube) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                tube.tryPaint(data.copyTag().getByteOr("pCol", (byte) 0));
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof PneumaticTubeBlockEntity tube) {
            byte color = tube.getPaintColor();
            if (color > 0) {
                for (ItemStack drop : drops) {
                    CompoundTag tag = new CompoundTag();
                    CustomData existing = drop.get(DataComponents.CUSTOM_DATA);
                    if (existing != null) {
                        tag = existing.copyTag();
                    }
                    tag.putByte("pCol", color);
                    drop.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                }
            }
        }
        return drops;
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(PneumaticTubeBlock::new);
    }
    *///?} else {
    /*@Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beFactory.apply(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        BlockEntityType<? extends PneumaticTubeBlockEntity> expected =
                this.tickerRef == null ? ModBlockEntities.PNEUMATIC_TUBE.get() : null;
        // Resolve lazily after registry is ready via override in subclasses when needed
        if (this instanceof RestrictionTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.RESTRICTION_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof MagTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.MAG_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof AcceleratorBlock) {
            return createTickerHelper(type, ModBlockEntities.ACCELERATOR.get(), PneumaticTubeBlockEntity::tick);
        }
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE.get(), PneumaticTubeBlockEntity::tick);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(PneumaticTubeBlock::new);
    }
    *///?}

    //? if <1.21.11 {
    @Override
    public void onRemove(BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                         BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                com.rptubes.blockentity.Removal.markBroken(be);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    //?}
}
