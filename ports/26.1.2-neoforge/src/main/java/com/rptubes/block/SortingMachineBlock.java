package com.rptubes.block;

import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SortingMachineBlock extends MachineBlock {
    public SortingMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SortingMachineBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.SORTING_MACHINE.get(),
                        (lvl, pos, st, be) -> com.rptubes.blockentity.TransposerBlockEntity.serverTick(lvl, pos, st, be));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SortingMachineBlockEntity sorter && player instanceof ServerPlayer sp) {
                sp.openMenu(sorter, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SortingMachineBlockEntity sorter) {
            sorter.onNeighborChange();
        }
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(SortingMachineBlock::new);
    }
}