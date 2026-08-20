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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Port of Tubestuff's ACT Mk II block — no facing, crafts continuously via ticker. */
public class AutoCraftingTableMk2Block extends Block implements EntityBlock {
    public AutoCraftingTableMk2Block(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.world.level.BlockGetter world) {
        return new AutoCraftingTableMk2BlockEntity();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AutoCraftingTableMk2BlockEntity && player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer) player;
                AutoCraftingTableMk2BlockEntity table = (AutoCraftingTableMk2BlockEntity) be;
                MenuHelper.open(sp, table, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
