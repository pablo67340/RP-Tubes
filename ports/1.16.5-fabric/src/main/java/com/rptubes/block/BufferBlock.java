package com.rptubes.block;

import net.minecraft.world.level.block.EntityBlock;

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

public class BufferBlock extends MachineBlock implements EntityBlock {
    public BufferBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.world.level.BlockGetter world) {
        return new BufferBlockEntity();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferBlockEntity && player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer) player;
                BufferBlockEntity buffer = (BufferBlockEntity) be;
                MenuHelper.open(sp, buffer, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
