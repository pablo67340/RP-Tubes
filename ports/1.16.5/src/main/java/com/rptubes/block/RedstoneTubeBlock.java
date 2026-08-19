package com.rptubes.block;

import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.IntegerProperty;

/**
 * Pneumatic tube that also weakly conducts redstone along its connections.
 * Works like redstone dust: power is cached on the block entity and only
 * recomputed on neighbor changes, with a reentrancy guard so tubes never
 * recursively evaluate each other (OG used RedPowerLib.isSearching()).
 */
public class RedstoneTubeBlock extends PneumaticTubeBlock {
    /** Visual glow level, OG ps = 0..3 (tiles 68+ps / 72+ps). */
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 3);

    /** False while a tube is scanning neighbors, so other tubes report 0. */
    private static boolean shouldSignal = true;

    public RedstoneTubeBlock(Properties properties) {
        super(properties, RedstoneTubeBlockEntity::new);
        this.registerDefaultState(this.defaultBlockState().setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
        if (!shouldSignal) {
            return 0;
        }
        TileEntity be = level.getBlockEntity(pos);
        return be instanceof RedstoneTubeBlockEntity ? ((RedstoneTubeBlockEntity) be).getPower() : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
        // OG tubes power weakly only (isBlockWeakPoweringTo)
        return 0;
    }

    @Override
    public void onPlace(BlockState state, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && !oldState.is(this)) {
            this.updatePower(level, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            this.updatePower(level, pos);
        }
    }

    private void updatePower(World level, BlockPos pos) {
        TileEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RedstoneTubeBlockEntity)) {
            return;
        }
        RedstoneTubeBlockEntity tube = (RedstoneTubeBlockEntity) be;
        int target = calculateTargetPower(level, pos);
        if (tube.getPower() != target) {
            tube.setPower(target);
            // OG glow step: ps = (PowerState + 84) / 85 over 0..255 -> 0..3 over 0..15
            int glow = target == 0 ? 0 : Math.min(3, (target + 4) / 5);
            BlockState state = level.getBlockState(pos);
            if (state.is(this) && state.getValue(POWER) != glow) {
                level.setBlock(pos, state.setValue(POWER, glow), 2);
            }
            // Wakes adjacent tubes (propagation) and any listening machines
            level.updateNeighborsAt(pos, this);
        }
    }

    private static int calculateTargetPower(World level, BlockPos pos) {
        // Adjacent redstone tubes: cached power minus dust-like decay
        int wire = 0;
        for (Direction dir : Direction.values()) {
            BlockPos np = pos.relative(dir);
            if (level.getBlockState(np).getBlock() instanceof RedstoneTubeBlock
                    && level.getBlockEntity(np) instanceof RedstoneTubeBlockEntity) {
                        RedstoneTubeBlockEntity t = (RedstoneTubeBlockEntity) level.getBlockEntity(np);
                wire = Math.max(wire, t.getPower() - 1);
            }
        }
        // External sources, with tubes muted so nothing recurses back into us
        int external = 0;
        shouldSignal = false;
        try {
            for (Direction dir : Direction.values()) {
                BlockPos np = pos.relative(dir);
                if (level.getBlockState(np).getBlock() instanceof RedstoneTubeBlock) {
                    continue;
                }
                external = Math.max(external, level.getSignal(np, dir));
            }
        } finally {
            shouldSignal = true;
        }
        return Math.min(15, Math.max(external, wire));
    }
}
