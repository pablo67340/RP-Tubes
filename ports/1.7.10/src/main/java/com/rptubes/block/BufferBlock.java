package com.rptubes.block;

import com.rptubes.blockentity.BufferBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class BufferBlock extends MachineBlock {
    public BufferBlock() {
        super("buffer_front", "ejector_back", "buffer_side", "buffer_side", null, null, null);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new BufferBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            if (be instanceof BufferBlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                BufferBlockEntity buffer = (BufferBlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_BUFFER, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof BufferBlockEntity) {
            ((BufferBlockEntity) be).dropContents();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
