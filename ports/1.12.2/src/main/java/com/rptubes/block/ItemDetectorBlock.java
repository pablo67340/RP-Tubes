package com.rptubes.block;

import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

public class ItemDetectorBlock extends MachineBlock {
    public ItemDetectorBlock() {
        super();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ItemDetectorBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos);
            if (be instanceof ItemDetectorBlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                ItemDetectorBlockEntity detector = (ItemDetectorBlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_ITEM_DETECTOR, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof ItemDetectorBlockEntity && ((ItemDetectorBlockEntity) be).isEmitting()) {
            return 15;
        }
        return 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction) {
        return 0;
    }
}
