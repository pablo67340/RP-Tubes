package com.rptubes.block;

import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class RegulatorBlock extends MachineBlock {
    public RegulatorBlock() {
        super("item_detector_front", "machine_bottom2", "regulator_we", "regulator_ns", null, "regulator_we_active", "regulator_ns_active");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new RegulatorBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            if (be instanceof RegulatorBlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                RegulatorBlockEntity regulator = (RegulatorBlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_REGULATOR, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof RegulatorBlockEntity) {
            RegulatorBlockEntity regulator = (RegulatorBlockEntity) be;
            regulator.onNeighborChange();
        }
    }
}
