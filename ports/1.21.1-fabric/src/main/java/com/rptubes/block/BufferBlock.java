package com.rptubes.block;

import com.rptubes.util.MenuHelper;

import com.rptubes.blockentity.BufferBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BufferBlock extends MachineBlock {
    public BufferBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BufferBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferBlockEntity buffer && player instanceof ServerPlayer sp) {
                MenuHelper.open(sp, buffer, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(BufferBlock::new);
    }
}