package com.rptubes.block;
//? if fabric && >=26.1.2 {

/*import com.rptubes.util.MenuHelper;
*///?}

import com.rptubes.blockentity.RetrieverBlockEntity;
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
//? if <26.1.2 {
import net.minecraft.world.item.DyeItem;
//?} else if fabric {
/*import net.minecraft.world.item.DyeColor;
*///?} else {
/*import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
*///?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

public class RetrieverBlock extends MachineBlock {
    public RetrieverBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RetrieverBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVER.get(),
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVER,
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVER,
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVER.get(),
        *///?}
                        (lvl, pos, st, be) -> com.rptubes.blockentity.TransposerBlockEntity.serverTick(lvl, pos, st, be));
    }

    @Override
    //? if <1.20.6 {
    /*public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever && held.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide && retriever.applyColor(dye.getDyeColor().getId())) {
    *///?} else if <1.21.2 {
    protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever && held.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide && retriever.applyColor(dye.getDyeColor().getId())) {
    //?} else if <26.1.2 {
    /*protected net.minecraft.world.InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever && held.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide() && retriever.applyColor(dye.getDyeColor().getId())) {
    *///?} else if fabric {
    /*protected net.minecraft.world.InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        DyeColor dyeColor = held.get(net.minecraft.core.component.DataComponents.DYE);
        if (be instanceof RetrieverBlockEntity retriever && dyeColor != null) {
            if (!level.isClientSide() && retriever.applyColor(dyeColor.getId())) {
    *///?} else {
    /*protected net.minecraft.world.InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever && held.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide() && retriever.applyColor(DyeColor.getColor(held).getId())) {
    *///?}
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
            //? if fabric && <1.20.6 {
            /*return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            MenuHelper.open(sp, retriever, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
            *///?} else if fabric && >=1.21.2 && <26.1.2 {
            /*return InteractionResult.SUCCESS;
        }
        return super.useItemOn(held, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!level.isClientSide() && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            com.rptubes.util.MenuHelper.open(sp, retriever, pos);
        }
        return InteractionResult.SUCCESS;
            *///?} else if <1.20.6 {
            /*return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            NetworkHooks.openScreen(sp, retriever, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
            *///?} else if fabric && <1.21.2 {
            /*return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(held, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!level.isClientSide && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            MenuHelper.open(sp, retriever, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
            *///?} else if fabric {
            /*return InteractionResult.SUCCESS;
        }
        return super.useItemOn(held, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!level.isClientSide() && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            MenuHelper.open(sp, retriever, pos);
        }
        return InteractionResult.SUCCESS;
            *///?} else if <1.21.2 {
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(held, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!level.isClientSide && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            sp.openMenu(retriever, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
            //?} else {
            /*return InteractionResult.SUCCESS;
        }
        return super.useItemOn(held, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!level.isClientSide() && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            sp.openMenu(retriever, pos);
        }
        return InteractionResult.SUCCESS;
            *///?}
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                //? if <1.21.2 {
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
                                //?} else {
                                /*net.minecraft.world.level.block.Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
                                *///?}
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever) {
            retriever.onNeighborChange();
        }
    }
    //? if >=1.20.6 {


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(RetrieverBlock::new);
    }
    //?}
}
