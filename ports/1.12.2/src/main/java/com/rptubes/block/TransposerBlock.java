package com.rptubes.block;

import com.rptubes.blockentity.TransposerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

public class TransposerBlock extends MachineBlock {
    public TransposerBlock() {
        super();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TransposerBlockEntity();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof TransposerBlockEntity) {
            TransposerBlockEntity transposer = (TransposerBlockEntity) be;
            transposer.onNeighborChange();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof TransposerBlockEntity) {
            ((TransposerBlockEntity) be).dropContents();
        }
        super.breakBlock(world, pos, state);
    }
}
