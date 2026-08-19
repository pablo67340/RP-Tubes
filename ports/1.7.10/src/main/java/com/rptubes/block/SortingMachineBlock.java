package com.rptubes.block;

import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class SortingMachineBlock extends MachineBlock {
    public SortingMachineBlock() {
        super("sorting_machine_bottom", "sorting_machine_front", "sorting_machine_side", "sorting_machine_side", null, "sorting_machine_side_active", "sorting_machine_side_active");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new SortingMachineBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            if (be instanceof SortingMachineBlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                SortingMachineBlockEntity sorter = (SortingMachineBlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_SORTING_MACHINE, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof SortingMachineBlockEntity) {
            SortingMachineBlockEntity sorter = (SortingMachineBlockEntity) be;
            sorter.onNeighborChange();
        }
    }
}
