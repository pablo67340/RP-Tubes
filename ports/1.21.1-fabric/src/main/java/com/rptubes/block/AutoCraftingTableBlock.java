package com.rptubes.block;

import com.rptubes.util.MenuHelper;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AutoCraftingTableBlockEntity table && player instanceof ServerPlayer sp) {
                MenuHelper.open(sp, table, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return net.minecraft.world.level.block.BaseEntityBlock.simpleCodec(AutoCraftingTableBlock::new);
    }
}