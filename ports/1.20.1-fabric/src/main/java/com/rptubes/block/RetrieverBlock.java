package com.rptubes.block;

import com.rptubes.blockentity.RetrieverBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.rptubes.util.MenuHelper;

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
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.RETRIEVER,
                        (lvl, pos, st, be) -> com.rptubes.blockentity.TransposerBlockEntity.serverTick(lvl, pos, st, be));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever && held.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide && retriever.applyColor(dye.getDyeColor().getId())) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && be instanceof RetrieverBlockEntity retriever && player instanceof ServerPlayer sp) {
            MenuHelper.open(sp, retriever, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever) {
            retriever.onNeighborChange();
        }
    }
}
