package com.rptubes.block;

import com.rptubes.blockentity.TransposerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TransposerBlock extends MachineBlock {
    public TransposerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TransposerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.TRANSPOSER, TransposerBlockEntity::serverTick);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block,
                                net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TransposerBlockEntity transposer) {
            transposer.onNeighborChange();
        }
    }

    // Buffer drops are handled in the block entity's setRemoved

    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(TransposerBlock::new);
    }
}