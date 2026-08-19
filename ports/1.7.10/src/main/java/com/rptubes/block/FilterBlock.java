package com.rptubes.block;

import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class FilterBlock extends MachineBlock {
    public FilterBlock() {
        super("machine_bottom", "machine_port", "filter_face", "filter_face", null, "filter_face_active", "filter_face_active");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new FilterBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack held = player.getHeldItem();
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof FilterBlockEntity && held != null && held.getItem() instanceof ItemDye) {
            FilterBlockEntity filter = (FilterBlockEntity) be;
            int color = 15 - held.getItemDamage();
            if (!world.isRemote && filter.applyColor(color)) {
                if (!player.capabilities.isCreativeMode) {
                    held.splitStack(1);
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
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof FilterBlockEntity) {
            FilterBlockEntity filter = (FilterBlockEntity) be;
            filter.onNeighborChange();
        }
    }
}
