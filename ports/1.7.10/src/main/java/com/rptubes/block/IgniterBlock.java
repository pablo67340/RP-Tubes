package com.rptubes.block;

import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class IgniterBlock extends MachineBlock {
    public IgniterBlock() {
        super("igniter_front", "machine_back", "igniter_ns", "igniter_we", "igniter_front_active", null, null);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new IgniterBlockEntity();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof IgniterBlockEntity) {
            IgniterBlockEntity igniter = (IgniterBlockEntity) be;
            igniter.onNeighborChange();
        }
    }

    /** OG: when Rotation==0 (down) and Active, block is a fire source. */
    @Override
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
        if (world.getBlock(x, y, z) != this) {
            return false;
        }
        int meta = world.getBlockMetadata(x, y, z);
        return isActive(meta) && getFacing(meta) == ForgeDirection.DOWN;
    }
}
