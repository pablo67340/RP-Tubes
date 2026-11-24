package com.rptubes.block;

import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
//? if forge && <1.20.6 {
/*import net.minecraftforge.network.NetworkHooks;
*///?}

public class BatteryBoxBlock extends BaseEntityBlock {
    /** OG getStorageForRender: Storage * 8 / max, selecting side texture 129 + n. */
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 8);

    public BatteryBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryBoxBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.BATTERY_BOX.get(), BatteryBoxBlockEntity::serverTick);
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.BATTERY_BOX, BatteryBoxBlockEntity::serverTick);
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.BATTERY_BOX, BatteryBoxBlockEntity::serverTick);
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.BATTERY_BOX.get(), BatteryBoxBlockEntity::serverTick);
        *///?}
    }

    @Override
    //? if <1.20.6 {
    /*public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        boolean client = level.isClientSide;
    *///?} else if <1.21.11 {
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        boolean client = level.isClientSide;
    //?} else {
    /*protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        boolean client = level.isClientSide();
    *///?}
        if (!client && level.getBlockEntity(pos) instanceof BatteryBoxBlockEntity box
                && player instanceof ServerPlayer sp) {
            //? if forge && <1.20.6 {
            /*NetworkHooks.openScreen(sp, box, pos);
            *///?} else if forgelike {
            sp.openMenu(box, pos);
            //?} else {
            /*com.rptubes.util.MenuHelper.open(sp, box, pos);
            *///?}
        }
        //? if <1.21.2 {
        return InteractionResult.sidedSuccess(client);
        //?} else {
        /*return InteractionResult.SUCCESS;
        *///?}
    }

    //? if <1.21.11 {
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof BatteryBoxBlockEntity box) {
                net.minecraft.world.Containers.dropContents(level, pos, box);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
    //?}

    //? if >=1.20.6 {
    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BatteryBoxBlock::new);
    }
    //?}
}
