package com.rptubes.block;

import com.rptubes.util.MenuHelper;
import net.minecraft.world.level.block.EntityBlock;

import com.rptubes.blockentity.BatteryBoxBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class BatteryBoxBlock extends Block implements EntityBlock {
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
    public BlockEntity newBlockEntity(BlockGetter world) {
        return new BatteryBoxBlockEntity();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BatteryBoxBlockEntity && player instanceof ServerPlayer) {
                MenuHelper.open((ServerPlayer) player, (BatteryBoxBlockEntity) be, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BatteryBoxBlockEntity) {
                Containers.dropContents(level, pos, (BatteryBoxBlockEntity) be);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
