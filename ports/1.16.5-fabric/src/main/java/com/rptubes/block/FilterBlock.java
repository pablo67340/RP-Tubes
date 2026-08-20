package com.rptubes.block;

import net.minecraft.world.level.block.EntityBlock;

import com.rptubes.util.MenuHelper;

import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FilterBlock extends MachineBlock implements EntityBlock {
    public FilterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.world.level.BlockGetter world) {
        return new FilterBlockEntity();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FilterBlockEntity && held.getItem() instanceof DyeItem) {
            DyeItem dye = (DyeItem) held.getItem();
            FilterBlockEntity filter = (FilterBlockEntity) be;
            if (!level.isClientSide && filter.applyColor(dye.getDyeColor().getId())) {
                if (!player.abilities.instabuild) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && be instanceof FilterBlockEntity && player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer) player;
            FilterBlockEntity filter = (FilterBlockEntity) be;
            MenuHelper.open(sp, filter, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FilterBlockEntity) {
            FilterBlockEntity filter = (FilterBlockEntity) be;
            filter.onNeighborChange();
        }
    }
}
