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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Supplier;

/** Thin pneumatic tube. Connection mask mirrors RedPower TubeLib side bits. */
public class PneumaticTubeBlock extends Block implements EntityBlock {
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

    private final Supplier<? extends BlockEntity> beFactory;

    public PneumaticTubeBlock(Properties properties) {
        this(properties, PneumaticTubeBlockEntity::new);
    }

    public PneumaticTubeBlock(Properties properties, Supplier<? extends BlockEntity> beFactory) {
        super(properties);
        this.beFactory = beFactory;
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
        if (level instanceof Level) {
            Level realLevel = (Level) level;
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                    TubeLib.canConnect(realLevel, pos, direction));
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (!level.isClientSide) {
            BlockState updated = getConnectionState(level, state, pos);
            if (updated != state) {
                level.setBlock(pos, updated, 3);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            level.setBlock(pos, getConnectionState(level, state, pos), 3);
        }
    }

    protected BlockState getConnectionState(Level level, BlockState state, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), TubeLib.canConnect(level, pos, dir));
        }
        return state;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter world) {
        return this.beFactory.get();
    }
}
