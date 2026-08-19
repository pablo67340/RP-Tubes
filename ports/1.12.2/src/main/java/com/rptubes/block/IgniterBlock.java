package com.rptubes.block;

import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

public class IgniterBlock extends MachineBlock {
    public IgniterBlock() {
        super();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new IgniterBlockEntity();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof IgniterBlockEntity) {
            IgniterBlockEntity igniter = (IgniterBlockEntity) be;
            igniter.onNeighborChange();
        }
    }

    /** OG: when Rotation==0 (down) and Active, block is a fire source. */
    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this && state.getValue(ACTIVE) && state.getValue(FACING) == EnumFacing.DOWN;
    }
}
