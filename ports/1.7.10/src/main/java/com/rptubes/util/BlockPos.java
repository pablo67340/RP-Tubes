package com.rptubes.util;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Minimal immutable position, standing in for 1.8+'s BlockPos so the tube
 * logic ports across without rewriting every coordinate triple by hand.
 */
public final class BlockPos {
    private final int x;
    private final int y;
    private final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public BlockPos offset(ForgeDirection dir) {
        return new BlockPos(this.x + dir.offsetX, this.y + dir.offsetY, this.z + dir.offsetZ);
    }

    public BlockPos offset(ForgeDirection dir, int n) {
        return new BlockPos(this.x + dir.offsetX * n, this.y + dir.offsetY * n, this.z + dir.offsetZ * n);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockPos)) {
            return false;
        }
        BlockPos other = (BlockPos) o;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public int hashCode() {
        return (this.y + this.z * 31) * 31 + this.x;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
