package com.rptubes.block;

import com.rptubes.blockentity.DeployerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import com.rptubes.util.MenuHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DeployerBlock extends MachineBlock {
    public DeployerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DeployerBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        // OG deployer works on Rotation^1 — its working face points AT the placer.
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DeployerBlockEntity deployer && player instanceof ServerPlayer sp) {
                MenuHelper.open(sp, deployer, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DeployerBlockEntity deployer) {
            deployer.onNeighborChange();
        }
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(DeployerBlock::new);
    }
}