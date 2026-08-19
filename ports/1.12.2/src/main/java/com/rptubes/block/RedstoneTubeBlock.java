package com.rptubes.block;

import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Pneumatic tube that also weakly conducts redstone along its connections.
 * Works like redstone dust: power is cached on the block entity and only
 * recomputed on neighbor changes, with a reentrancy guard so tubes never
 * recursively evaluate each other (OG used RedPowerLib.isSearching()).
 */
public class RedstoneTubeBlock extends PneumaticTubeBlock {
    /** Visual glow level, OG ps = 0..3 (tiles 68+ps / 72+ps). */
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 3);

    /** False while a tube is scanning neighbors, so other tubes report 0. */
    private static boolean shouldSignal = true;

    public RedstoneTubeBlock() {
        super(RedstoneTubeBlockEntity::new);
        this.setDefaultState(this.getDefaultState().withProperty(POWER, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, UP, DOWN, POWER);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWER, meta & 3);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (!shouldSignal) {
            return 0;
        }
        TileEntity be = world.getTileEntity(pos);
        return be instanceof RedstoneTubeBlockEntity ? ((RedstoneTubeBlockEntity) be).getPower() : 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        // OG tubes power weakly only (isBlockWeakPoweringTo)
        return 0;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) {
            this.updatePower(world, pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);
        if (!world.isRemote) {
            this.updatePower(world, pos);
        }
    }

    private void updatePower(World world, BlockPos pos) {
        TileEntity be = world.getTileEntity(pos);
        if (!(be instanceof RedstoneTubeBlockEntity)) {
            return;
        }
        RedstoneTubeBlockEntity tube = (RedstoneTubeBlockEntity) be;
        int target = calculateTargetPower(world, pos);
        if (tube.getPower() != target) {
            tube.setPower(target);
            // OG glow step: ps = (PowerState + 84) / 85 over 0..255 -> 0..3 over 0..15
            int glow = target == 0 ? 0 : Math.min(3, (target + 4) / 5);
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == this && state.getValue(POWER) != glow) {
                world.setBlockState(pos, state.withProperty(POWER, glow), 2);
            }
            // Wakes adjacent tubes (propagation) and any listening machines
            world.notifyNeighborsOfStateChange(pos, this, false);
        }
    }

    private static int calculateTargetPower(World world, BlockPos pos) {
        // Adjacent redstone tubes: cached power minus dust-like decay
        int wire = 0;
        for (EnumFacing dir : EnumFacing.values()) {
            BlockPos np = pos.offset(dir);
            if (world.getBlockState(np).getBlock() instanceof RedstoneTubeBlock
                    && world.getTileEntity(np) instanceof RedstoneTubeBlockEntity) {
                RedstoneTubeBlockEntity t = (RedstoneTubeBlockEntity) world.getTileEntity(np);
                wire = Math.max(wire, t.getPower() - 1);
            }
        }
        // External sources, with tubes muted so nothing recurses back into us
        int external = 0;
        shouldSignal = false;
        try {
            for (EnumFacing dir : EnumFacing.values()) {
                BlockPos np = pos.offset(dir);
                if (world.getBlockState(np).getBlock() instanceof RedstoneTubeBlock) {
                    continue;
                }
                external = Math.max(external, world.getRedstonePower(np, dir));
            }
        } finally {
            shouldSignal = true;
        }
        return Math.min(15, Math.max(external, wire));
    }
}
