package com.rptubes.compat.cbm;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

/** The world as the tube block renderer expects it: the part's own position reads as the tube block. */
@SideOnly(Side.CLIENT)
final class PartAccess implements IBlockAccess {
    private final TubePart part;
    private final World world;

    PartAccess(TubePart part) {
        this.part = part;
        this.world = part.world();
    }

    private boolean self(int x, int y, int z) {
        return x == this.part.x() && y == this.part.y() && z == this.part.z();
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return this.self(x, y, z) ? this.part.block() : this.world.getBlock(x, y, z);
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return this.world.getTileEntity(x, y, z);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int light) {
        return this.world.getLightBrightnessForSkyBlocks(x, y, z, light);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        return this.self(x, y, z) ? this.part.renderMeta() : this.world.getBlockMetadata(x, y, z);
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
        return this.world.isBlockProvidingPowerTo(x, y, z, side);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return !this.self(x, y, z) && this.world.isAirBlock(x, y, z);
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return this.world.getBiomeGenForCoords(x, z);
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return this.world.extendedLevelsInChunkCache();
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return this.world.isSideSolid(x, y, z, side, _default);
    }
}
