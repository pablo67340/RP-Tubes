package com.rptubes.block;
//? if fabric && >=26.1.2 {

/*import com.rptubes.util.MenuHelper;
*///?}

import com.rptubes.blockentity.RetrievulatorBlockEntity;
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

public class RetrievulatorBlock extends MachineBlock {
    public RetrievulatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RetrievulatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVULATOR.get(), RetrievulatorBlockEntity::serverTick);
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVULATOR, RetrievulatorBlockEntity::serverTick);
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVULATOR, RetrievulatorBlockEntity::serverTick);
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVULATOR.get(), RetrievulatorBlockEntity::serverTick);
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
            if (be instanceof RetrievulatorBlockEntity retrievulator && player instanceof ServerPlayer sp) {
                //? if forge && <1.20.6 {
                /*NetworkHooks.openScreen(sp, retrievulator, pos);
                *///?} else if forgelike {
                sp.openMenu(retrievulator, pos);
                //?} else if >=1.21.11 && <26.1.2 {
                /*com.rptubes.util.MenuHelper.open(sp, retrievulator, pos);
                *///?} else {
                /*MenuHelper.open(sp, retrievulator, pos);
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

    /** Powers only the Retriever behind it, and only while pulsing. */
    @Override
    public int getSignal(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos,
                         net.minecraft.core.Direction direction) {
        return state.getValue(ACTIVE) && direction == state.getValue(FACING) ? 15 : 0;
    }
    //? if >=1.20.6 {


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(RetrievulatorBlock::new);
    }
    //?}
}
