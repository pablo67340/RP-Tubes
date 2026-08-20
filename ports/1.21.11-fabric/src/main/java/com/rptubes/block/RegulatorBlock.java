package com.rptubes.block;

import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import com.rptubes.util.MenuHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RegulatorBlock extends MachineBlock {
    public RegulatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RegulatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.REGULATOR, RegulatorBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RegulatorBlockEntity regulator && player instanceof ServerPlayer sp) {
                com.rptubes.util.MenuHelper.open(sp, regulator, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RegulatorBlockEntity regulator) {
            regulator.onNeighborChange();
        }
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(RegulatorBlock::new);
    }
}