package com.rptubes.block;

import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

public class FilterBlock extends MachineBlock {
    public FilterBlock() {
        super();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new FilterBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof FilterBlockEntity && held.getItem() instanceof ItemDye) {
            FilterBlockEntity filter = (FilterBlockEntity) be;
            int color = net.minecraft.item.EnumDyeColor.byDyeDamage(held.getMetadata()).getMetadata();
            if (!world.isRemote && filter.applyColor(color)) {
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
            }
            return true;
        }
        if (!world.isRemote && be instanceof FilterBlockEntity && player instanceof EntityPlayerMP) {
            EntityPlayerMP sp = (EntityPlayerMP) player;
            FilterBlockEntity filter = (FilterBlockEntity) be;
            player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_FILTER, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof FilterBlockEntity) {
            FilterBlockEntity filter = (FilterBlockEntity) be;
            filter.onNeighborChange();
        }
    }
}
