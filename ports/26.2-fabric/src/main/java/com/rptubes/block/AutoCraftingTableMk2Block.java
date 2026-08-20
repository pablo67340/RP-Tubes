package com.rptubes.block;

import com.rptubes.util.MenuHelper;

import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
        return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.ACT_MK2, AutoCraftingTableMk2BlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AutoCraftingTableMk2BlockEntity table && player instanceof ServerPlayer sp) {
                MenuHelper.open(sp, table, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(AutoCraftingTableMk2Block::new);
    }
}