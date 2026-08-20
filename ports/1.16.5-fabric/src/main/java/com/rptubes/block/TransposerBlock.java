package com.rptubes.block;

import net.minecraft.world.level.block.EntityBlock;

import com.rptubes.blockentity.TransposerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TransposerBlock extends MachineBlock implements EntityBlock {
    public TransposerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.world.level.BlockGetter world) {
        return new TransposerBlockEntity();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block,
                                BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TransposerBlockEntity) {
            TransposerBlockEntity transposer = (TransposerBlockEntity) be;
            transposer.onNeighborChange();
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TransposerBlockEntity) {
                // buffer drops handled in setRemoved
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
