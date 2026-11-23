package com.rptubes.block;

import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
//? if fabric && <1.21.11 {
/*import net.minecraft.world.level.Level;
*///?} else {
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//?}
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class IgniterBlock extends MachineBlock {
    public IgniterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IgniterBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        // OG igniter works on Rotation^1 — its working face points AT the placer.
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.IGNITER.get(), IgniterBlockEntity::serverTick);
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.IGNITER, IgniterBlockEntity::serverTick);
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.IGNITER, IgniterBlockEntity::serverTick);
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.IGNITER.get(), IgniterBlockEntity::serverTick);
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
        if (be instanceof IgniterBlockEntity igniter) {
            igniter.onNeighborChange();
        }
    }

    //? if forge && <1.20.6 {
    /*/^* OG: when Rotation==0 (down) and Active, block is a fire source. ^/
    @Override
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        return state.getValue(ACTIVE) && state.getValue(FACING) == Direction.DOWN;
    }
    *///?} else if forgelike {
    /** OG: when Rotation==0 (down) and Active, block is a fire source. */
    @Override
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        return state.getValue(ACTIVE) && state.getValue(FACING) == Direction.DOWN;
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(IgniterBlock::new);
    }
    //?} else if >=26.1.2 {
    /*/^* OG: when Rotation==0 (down) and Active, block is a fire source. ^/
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        return state.getValue(ACTIVE) && state.getValue(FACING) == Direction.DOWN;
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(IgniterBlock::new);
    }
    *///?} else if >=1.21.11 {
    /*/^* OG: when Rotation==0 (down) and Active, block is a fire source. Forge-only hook; unused on Fabric. ^/
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        return state.getValue(ACTIVE) && state.getValue(FACING) == Direction.DOWN;
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(IgniterBlock::new);
    }
    *///?} else if >=1.20.6 {
    /*@Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(IgniterBlock::new);
    }
    *///?}
}
