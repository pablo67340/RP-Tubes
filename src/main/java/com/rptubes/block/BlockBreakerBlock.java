package com.rptubes.block;

import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreakerBlock extends MachineBlock {
    public BlockBreakerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockBreakerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.BLOCK_BREAKER.get(), BlockBreakerBlockEntity::serverTick);
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.BLOCK_BREAKER, BlockBreakerBlockEntity::serverTick);
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.BLOCK_BREAKER, BlockBreakerBlockEntity::serverTick);
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.BLOCK_BREAKER.get(), BlockBreakerBlockEntity::serverTick);
        *///?}
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                //? if <1.21.11 {
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
                                //?} else {
                                /*net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
                                *///?}
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockBreakerBlockEntity breaker) {
            breaker.onNeighborChange();
        }
    }
    //? if >=1.20.6 {


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(BlockBreakerBlock::new);
    }
    //?}
}
