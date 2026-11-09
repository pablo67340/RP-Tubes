package com.rptubes.block;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.TubeLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
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

import java.util.List;
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
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (level instanceof Level realLevel) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                    TubeLib.canConnect(realLevel, pos, direction));
        }
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
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
        if (this instanceof RedstoneTubeBlock) {
            return createTickerHelper(type, ModBlockEntities.REDSTONE_TUBE.get(), PneumaticTubeBlockEntity::tick);
        }
        return createTickerHelper(type, ModBlockEntities.PNEUMATIC_TUBE.get(), PneumaticTubeBlockEntity::tick);
    }
}
