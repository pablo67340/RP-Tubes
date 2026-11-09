package com.rptubes.block;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.TubeLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    private final BlockEntityType.BlockEntitySupplier<? extends PneumaticTubeBlockEntity> tickerRef;

    public PneumaticTubeBlock(Properties properties) {
        this(properties, PneumaticTubeBlockEntity::new, null);
    }

    public PneumaticTubeBlock(Properties properties,
                              BiFunction<BlockPos, BlockState, BlockEntity> beFactory,
                              BlockEntityType.BlockEntitySupplier<? extends PneumaticTubeBlockEntity> tickerRef) {
        super(properties);
        this.beFactory = beFactory;
        this.tickerRef = tickerRef;
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
    protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader level,
                                     net.minecraft.world.level.ScheduledTickAccess tickAccess, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState,
                                     net.minecraft.util.RandomSource random) {
        if (level instanceof Level realLevel) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                    TubeLib.canConnect(realLevel, pos, direction));
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation,
                                boolean isMoving) {
        if (!level.isClientSide()) {
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


    @Override
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
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE, PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE, PneumaticTubeBlockEntity::tick);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(PneumaticTubeBlock::new);
    }
}