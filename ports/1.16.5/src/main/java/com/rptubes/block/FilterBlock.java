package com.rptubes.block;

import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkHooks;

public class FilterBlock extends MachineBlock {
    public FilterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new FilterBlockEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player,
                                 Hand hand, BlockRayTraceResult hit) {
        ItemStack held = player.getItemInHand(hand);
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof FilterBlockEntity && held.getItem() instanceof DyeItem) {
            DyeItem dye = (DyeItem) held.getItem();
            FilterBlockEntity filter = (FilterBlockEntity) be;
            if (!level.isClientSide && filter.applyColor(dye.getDyeColor().getId())) {
                if (!player.abilities.instabuild) {
                    held.shrink(1);
                }
            }
            return ActionResultType.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && be instanceof FilterBlockEntity && player instanceof ServerPlayerEntity) {
            ServerPlayerEntity sp = (ServerPlayerEntity) player;
            FilterBlockEntity filter = (FilterBlockEntity) be;
            NetworkHooks.openGui(sp, filter, pos);
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos,
                                net.minecraft.block.Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof FilterBlockEntity) {
            FilterBlockEntity filter = (FilterBlockEntity) be;
            filter.onNeighborChange();
        }
    }
}
