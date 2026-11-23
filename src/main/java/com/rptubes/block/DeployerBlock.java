package com.rptubes.block;
//? if (fabric && >=26.1.2) || (fabric && >=1.21.1 && <1.21.11) {

/*import com.rptubes.util.MenuHelper;
*///?}

import com.rptubes.blockentity.DeployerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
//? if (fabric && >=1.20.6 && <1.21.1) || (fabric && >=1.21.11 && <26.1.2) {
/*import com.rptubes.util.MenuHelper;
*///?}
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
//? if forge && <1.20.6 {
/*import net.minecraftforge.network.NetworkHooks;
*///?} else if <1.20.6 {
/*import com.rptubes.util.MenuHelper;
*///?}

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
    //? if <1.20.6 {
    /*public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
    *///?} else if <1.21.11 {
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
    //?} else {
    /*protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
    *///?}
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DeployerBlockEntity deployer && player instanceof ServerPlayer sp) {
                //? if forge && <1.20.6 {
                /*NetworkHooks.openScreen(sp, deployer, pos);
                *///?} else if forgelike {
                sp.openMenu(deployer, pos);
                //?} else if >=1.21.11 && <26.1.2 {
                /*com.rptubes.util.MenuHelper.open(sp, deployer, pos);
                *///?} else {
                /*MenuHelper.open(sp, deployer, pos);
                *///?}
            }
        }
        //? if <1.21.11 {
        return InteractionResult.sidedSuccess(level.isClientSide);
        //?} else {
        /*return InteractionResult.SUCCESS;
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
        if (be instanceof DeployerBlockEntity deployer) {
            deployer.onNeighborChange();
        }
    }
    //? if >=1.20.6 {


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(DeployerBlock::new);
    }
    //?}
}
