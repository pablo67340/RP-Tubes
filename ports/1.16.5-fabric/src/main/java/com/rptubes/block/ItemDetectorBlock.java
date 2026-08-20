package com.rptubes.block;

import net.minecraft.world.level.block.EntityBlock;

import com.rptubes.util.MenuHelper;

import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ItemDetectorBlock extends MachineBlock implements EntityBlock {
    public ItemDetectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.world.level.BlockGetter world) {
        return new ItemDetectorBlockEntity();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ItemDetectorBlockEntity && player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer) player;
                ItemDetectorBlockEntity detector = (ItemDetectorBlockEntity) be;
                MenuHelper.open(sp, detector, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ItemDetectorBlockEntity && ((ItemDetectorBlockEntity) be).isEmitting()) {
            return 15;
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }
}
