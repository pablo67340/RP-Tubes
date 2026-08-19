package com.rptubes.block;

import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class BlockBreakerBlock extends MachineBlock {
    public BlockBreakerBlock() {
        super("machine_bottom", "machine_port", "transposer_face", "transposer_face", null, null, null);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new BlockBreakerBlockEntity();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof BlockBreakerBlockEntity) {
            BlockBreakerBlockEntity breaker = (BlockBreakerBlockEntity) be;
            breaker.onNeighborChange();
        }
    }
}
