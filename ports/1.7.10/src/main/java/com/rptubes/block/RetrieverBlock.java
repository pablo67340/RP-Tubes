package com.rptubes.block;

import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class RetrieverBlock extends MachineBlock {
    public RetrieverBlock() {
        super("retriever_back", "retriever_front", "retriever_side", "retriever_side", null, "retriever_side_active", "retriever_side_active");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new RetrieverBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack held = player.getHeldItem();
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof RetrieverBlockEntity && held != null && held.getItem() instanceof ItemDye) {
            RetrieverBlockEntity retriever = (RetrieverBlockEntity) be;
            int color = 15 - held.getItemDamage();
            if (!world.isRemote && retriever.applyColor(color)) {
                if (!player.capabilities.isCreativeMode) {
                    held.splitStack(1);
                }
            }
            return true;
        }
        if (!world.isRemote && be instanceof RetrieverBlockEntity && player instanceof EntityPlayerMP) {
            EntityPlayerMP sp = (EntityPlayerMP) player;
            RetrieverBlockEntity retriever = (RetrieverBlockEntity) be;
            player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_RETRIEVER, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof RetrieverBlockEntity) {
            RetrieverBlockEntity retriever = (RetrieverBlockEntity) be;
            retriever.onNeighborChange();
        }
    }
}
