package com.rptubes.block;
//? if (fabric && >=26.1.2) || (fabric && >=1.21.1 && <1.21.11) {

/*import com.rptubes.util.MenuHelper;
*///?}

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
//? if (fabric && >=1.20.6 && <1.21.1) || (fabric && >=1.21.11 && <26.1.2) {
/*import com.rptubes.util.MenuHelper;
*///?}
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
//? if forge && <1.20.6 {
/*import net.minecraftforge.network.NetworkHooks;
*///?} else if <1.20.6 {
/*import com.rptubes.util.MenuHelper;
*///?}

/** Port of BuildCraft BlockAutoWorkbench — no facing, same texture layout as the original. */
public class AutoCraftingTableBlock extends BaseEntityBlock {
    public AutoCraftingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoCraftingTableBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
            if (be instanceof AutoCraftingTableBlockEntity table && player instanceof ServerPlayer sp) {
                //? if forge && <1.20.6 {
                /*NetworkHooks.openScreen(sp, table, pos);
                *///?} else if forgelike {
                sp.openMenu(table, pos);
                //?} else if >=1.21.11 && <26.1.2 {
                /*com.rptubes.util.MenuHelper.open(sp, table, pos);
                *///?} else {
                /*MenuHelper.open(sp, table, pos);
                *///?}
            }
        }
        //? if <1.20.6 {
        /*return InteractionResult.sidedSuccess(level.isClientSide);
    }
        *///?} else if <1.21.11 {
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(AutoCraftingTableBlock::new);
    }
        //?} else {
        /*return InteractionResult.SUCCESS;
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(AutoCraftingTableBlock::new);
    }
        *///?}

    //? if <1.21.11 {
    @Override
    public void onRemove(BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                         BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                com.rptubes.blockentity.Removal.markBroken(be);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    //?}
}
