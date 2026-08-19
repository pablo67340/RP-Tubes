package com.rptubes.block;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.TubeLib;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.block.Block;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Supplier;

/** Thin pneumatic tube. Connection mask mirrors RedPower TubeLib side bits. */
public class PneumaticTubeBlock extends Block {
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final BooleanProperty UP = SixWayBlock.UP;
    public static final BooleanProperty DOWN = SixWayBlock.DOWN;

    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION;

    // Match OG RenderTube doubleBox bounds: 0.25..0.75 => 4..12
    private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
    private static final VoxelShape ARM_N = Block.box(4, 4, 0, 12, 12, 4);
    private static final VoxelShape ARM_S = Block.box(4, 4, 12, 12, 12, 16);
    private static final VoxelShape ARM_W = Block.box(0, 4, 4, 4, 12, 12);
    private static final VoxelShape ARM_E = Block.box(12, 4, 4, 16, 12, 12);
    private static final VoxelShape ARM_D = Block.box(4, 0, 4, 12, 4, 12);
    private static final VoxelShape ARM_U = Block.box(4, 12, 4, 12, 16, 12);

    private final Supplier<? extends TileEntity> beFactory;

    public PneumaticTubeBlock(Properties properties) {
        this(properties, PneumaticTubeBlockEntity::new);
    }

    public PneumaticTubeBlock(Properties properties, Supplier<? extends TileEntity> beFactory) {
        super(properties);
        this.beFactory = beFactory;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
                .setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH)) shape = VoxelShapes.or(shape, ARM_N);
        if (state.getValue(SOUTH)) shape = VoxelShapes.or(shape, ARM_S);
        if (state.getValue(WEST)) shape = VoxelShapes.or(shape, ARM_W);
        if (state.getValue(EAST)) shape = VoxelShapes.or(shape, ARM_E);
        if (state.getValue(DOWN)) shape = VoxelShapes.or(shape, ARM_D);
        if (state.getValue(UP)) shape = VoxelShapes.or(shape, ARM_U);
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getConnectionState(context.getLevel(), this.defaultBlockState(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  IWorld level, BlockPos pos, BlockPos neighborPos) {
        if (level instanceof World) {
            World realLevel = (World) level;
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                    TubeLib.canConnect(realLevel, pos, direction));
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (!level.isClientSide) {
            BlockState updated = getConnectionState(level, state, pos);
            if (updated != state) {
                level.setBlock(pos, updated, 3);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            level.setBlock(pos, getConnectionState(level, state, pos), 3);
        }
    }

    protected BlockState getConnectionState(World level, BlockState state, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), TubeLib.canConnect(level, pos, dir));
        }
        return state;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.beFactory.get();
    }
}
