package com.rptubes.block;
//? if fabric && >=26.1.2 {

/*import com.rptubes.util.MenuHelper;
*///?}

import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.registry.ModBlockEntities;
//? if fabric && >=1.21.1 && <1.21.11 {
/*import com.rptubes.util.MenuHelper;
*///?}
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
//? if forge && <1.20.6 {
/*import net.minecraftforge.network.NetworkHooks;
*///?} else if <1.20.6 {
/*import com.rptubes.util.MenuHelper;
*///?}

/** Port of Tubestuff's ACT Mk II block — no facing, crafts continuously via ticker. */
public class AutoCraftingTableMk2Block extends BaseEntityBlock {
    public AutoCraftingTableMk2Block(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoCraftingTableMk2BlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.ACT_MK2.get(), AutoCraftingTableMk2BlockEntity::serverTick);
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.ACT_MK2, AutoCraftingTableMk2BlockEntity::serverTick);
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.ACT_MK2, AutoCraftingTableMk2BlockEntity::serverTick);
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.ACT_MK2.get(), AutoCraftingTableMk2BlockEntity::serverTick);
        *///?}
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
            if (be instanceof AutoCraftingTableMk2BlockEntity table && player instanceof ServerPlayer sp) {
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
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(AutoCraftingTableMk2Block::new);
    }
        //?} else {
        /*return InteractionResult.SUCCESS;
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(AutoCraftingTableMk2Block::new);
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
