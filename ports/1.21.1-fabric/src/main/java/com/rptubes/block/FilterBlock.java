package com.rptubes.block;

import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.util.MenuHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FilterBlock extends MachineBlock {
    public FilterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FilterBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.FILTER,
                        (lvl, pos, st, be) -> com.rptubes.blockentity.TransposerBlockEntity.serverTick(lvl, pos, st, be));
    }

    @Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FilterBlockEntity filter && held.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide && filter.applyColor(dye.getDyeColor().getId())) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(held, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!level.isClientSide && be instanceof FilterBlockEntity filter && player instanceof ServerPlayer sp) {
            MenuHelper.open(sp, filter, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FilterBlockEntity filter) {
            filter.onNeighborChange();
        }
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(FilterBlock::new);
    }
}