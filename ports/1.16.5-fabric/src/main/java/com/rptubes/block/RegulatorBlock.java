package com.rptubes.block;

import net.minecraft.world.level.block.EntityBlock;

import com.rptubes.util.MenuHelper;

import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RegulatorBlock extends MachineBlock implements EntityBlock {
    public RegulatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.world.level.BlockGetter world) {
        return new RegulatorBlockEntity();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RegulatorBlockEntity && player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer) player;
                RegulatorBlockEntity regulator = (RegulatorBlockEntity) be;
                MenuHelper.open(sp, regulator, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RegulatorBlockEntity) {
            RegulatorBlockEntity regulator = (RegulatorBlockEntity) be;
            regulator.onNeighborChange();
        }
    }
}
