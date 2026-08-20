package com.rptubes.block;

import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        if (!shouldSignal) {
            return 0;
        }
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof RedstoneTubeBlockEntity tube ? tube.getPower() : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        // OG tubes power weakly only (isBlockWeakPoweringTo)
        return 0;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide() && !oldState.is(this)) {
            this.updatePower(level, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation,
                                boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        if (!level.isClientSide()) {
            this.updatePower(level, pos);
        }
    }

    private void updatePower(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RedstoneTubeBlockEntity tube)) {
            return;
        }
        int target = calculateTargetPower(level, pos);
        if (tube.getPower() != target) {
            tube.setPower(target);
            // OG glow step: ps = (PowerState + 84) / 85 over 0..255 -> 0..3 over 0..15
            int glow = target == 0 ? 0 : Math.min(3, (target + 4) / 5);
            BlockState state = level.getBlockState(pos);
            if (state.is(this) && state.getValue(POWER) != glow) {
                level.setBlock(pos, state.setValue(POWER, glow), Block.UPDATE_CLIENTS);
            }
            // Wakes adjacent tubes (propagation) and any listening machines
            level.updateNeighborsAt(pos, this);
        }
    }

    private static int calculateTargetPower(Level level, BlockPos pos) {
        // Adjacent redstone tubes: cached power minus dust-like decay
        int wire = 0;
        for (Direction dir : Direction.values()) {
            BlockPos np = pos.relative(dir);
            if (level.getBlockState(np).getBlock() instanceof RedstoneTubeBlock
                    && level.getBlockEntity(np) instanceof RedstoneTubeBlockEntity t) {
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
