package com.rptubes.block;

import com.rptubes.blockentity.RetrieverBlockEntity;
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

public class RetrieverBlock extends MachineBlock {
    public RetrieverBlock() {
        super();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new RetrieverBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof RetrieverBlockEntity && held.getItem() instanceof ItemDye) {
            RetrieverBlockEntity retriever = (RetrieverBlockEntity) be;
            int color = net.minecraft.item.EnumDyeColor.byDyeDamage(held.getMetadata()).getMetadata();
            if (!world.isRemote && retriever.applyColor(color)) {
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
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
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof RetrieverBlockEntity) {
            RetrieverBlockEntity retriever = (RetrieverBlockEntity) be;
            retriever.onNeighborChange();
        }
    }
}
