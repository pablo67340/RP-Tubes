package com.rptubes.blockentity;

import com.rptubes.util.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Base tile with 1.8+-style accessors so ported code keeps its shape.
 */
public abstract class CompatTileEntity extends TileEntity {

    public World getWorld() {
        return this.worldObj;
    }

    public BlockPos getPos() {
        return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
    }
}
