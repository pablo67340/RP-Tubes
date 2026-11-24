package com.rptubes.block;
//? if fabric && >=26.1.2 {

/*import com.rptubes.util.MenuHelper;
*///?}

import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.registry.ModBlockEntities;
//? if fabric && >=1.21.1 && <1.21.11 {
/*import com.rptubes.util.MenuHelper;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
//? if (fabric && >=1.20.6 && <1.21.1) || (fabric && >=1.21.11 && <26.1.2) {
/*import com.rptubes.util.MenuHelper;
*///?}
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
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

public class ItemDetectorBlock extends MachineBlock {
    public ItemDetectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemDetectorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.ITEM_DETECTOR.get(), ItemDetectorBlockEntity::serverTick);
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.ITEM_DETECTOR, ItemDetectorBlockEntity::serverTick);
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.ITEM_DETECTOR, ItemDetectorBlockEntity::serverTick);
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.ITEM_DETECTOR.get(), ItemDetectorBlockEntity::serverTick);
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
            if (be instanceof ItemDetectorBlockEntity detector && player instanceof ServerPlayer sp) {
                //? if forge && <1.20.6 {
                /*NetworkHooks.openScreen(sp, detector, pos);
                *///?} else if forgelike {
                sp.openMenu(detector, pos);
                //?} else if >=1.21.11 && <26.1.2 {
                /*com.rptubes.util.MenuHelper.open(sp, detector, pos);
                *///?} else {
                /*MenuHelper.open(sp, detector, pos);
                *///?}
            }
        }
        //? if <1.21.2 {
        return InteractionResult.sidedSuccess(level.isClientSide);
        //?} else {
        /*return InteractionResult.SUCCESS;
        *///?}
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ItemDetectorBlockEntity detector && detector.isEmitting()) {
            return 15;
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }
    //? if >=1.20.6 {


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(ItemDetectorBlock::new);
    }
    //?}
}
