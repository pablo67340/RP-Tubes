package com.rptubes.block;

import com.rptubes.blockentity.TransposerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;

public class TransposerBlock extends MachineBlock {
    public TransposerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new TransposerBlockEntity();
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos, net.minecraft.block.Block block,
                                BlockPos fromPos, boolean isMoving) {
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof TransposerBlockEntity) {
            TransposerBlockEntity transposer = (TransposerBlockEntity) be;
            transposer.onNeighborChange();
        }
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity be = level.getBlockEntity(pos);
            if (be instanceof TransposerBlockEntity) {
                // buffer drops handled in setRemoved
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
