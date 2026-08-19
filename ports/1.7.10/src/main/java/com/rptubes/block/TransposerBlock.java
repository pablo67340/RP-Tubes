package com.rptubes.block;

import com.rptubes.blockentity.TransposerBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class TransposerBlock extends MachineBlock {
    public TransposerBlock() {
        super("machine_bottom", "machine_port", "transposer_face", "transposer_face", null, "transposer_face_active", "transposer_face_active");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TransposerBlockEntity();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof TransposerBlockEntity) {
            TransposerBlockEntity transposer = (TransposerBlockEntity) be;
            transposer.onNeighborChange();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof TransposerBlockEntity) {
            ((TransposerBlockEntity) be).dropContents();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
