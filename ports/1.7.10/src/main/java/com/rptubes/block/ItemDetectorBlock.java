package com.rptubes.block;

import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class ItemDetectorBlock extends MachineBlock {
    public ItemDetectorBlock() {
        super("item_detector_front", "machine_bottom2", "item_detector_we", "item_detector_ns", null, "item_detector_we_active", "item_detector_ns_active");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new ItemDetectorBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            if (be instanceof ItemDetectorBlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                ItemDetectorBlockEntity detector = (ItemDetectorBlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_ITEM_DETECTOR, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity be = world.getTileEntity(x, y, z);
        if (be instanceof ItemDetectorBlockEntity && ((ItemDetectorBlockEntity) be).isEmitting()) {
            return 15;
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        return 0;
    }
}
