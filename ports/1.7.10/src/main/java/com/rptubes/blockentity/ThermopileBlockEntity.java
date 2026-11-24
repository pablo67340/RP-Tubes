package com.rptubes.blockentity;

import com.rptubes.power.PowerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Port of TileThermopile: output is 0.05 RF/t per degree of min(hot, cold), with the six
 * neighbours resampled every 20 ticks. Hot sides can melt ice and snow; a strong
 * hot/cold difference can quench adjacent lava.
 */
public class ThermopileBlockEntity extends PowerBlockEntity {
    private int tempHot;
    private int tempCold;
    private int ticks;
    private double fraction;

    public ThermopileBlockEntity() {
        super(1000, 0, 1000);
    }

    @Override
    protected void serverTick() {
        if (++this.ticks > 20) {
            this.ticks = 0;
            this.updateTemps(this.worldObj);
        }
        this.fraction += 0.05 * Math.min(this.tempHot, this.tempCold);
        long whole = (long) this.fraction;
        if (whole > 0) {
            this.fraction -= whole;
            this.buffer.generate(whole);
        }
        this.pushToNeighbours(Long.MAX_VALUE);
    }

    private void updateTemps(World world) {
        boolean nether = world.provider.isHellWorld;
        int hot = 0;
        int cold = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int x = this.xCoord + dir.offsetX;
            int y = this.yCoord + dir.offsetY;
            int z = this.zCoord + dir.offsetZ;
            Block block = world.getBlock(x, y, z);
            Material material = block.getMaterial();
            if (block.isAir(world, x, y, z)) {
                if (nether) {
                    hot++;
                } else {
                    cold++;
                }
            } else if (block == Blocks.ice || block == Blocks.packed_ice || block == Blocks.snow) {
                cold += 100;
            } else if (block == Blocks.snow_layer) {
                cold += 50;
            } else if (block == Blocks.torch) {
                hot += 5;
            } else if (block == Blocks.lit_pumpkin) {
                hot += 3;
            } else if (material == Material.water) {
                cold += 25;
            } else if (material == Material.lava) {
                hot += 100;
            } else if (block == Blocks.fire) {
                hot += 25;
            }
        }

        if (this.tempHot >= 100 && this.tempCold >= 200) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int x = this.xCoord + dir.offsetX;
                int y = this.yCoord + dir.offsetY;
                int z = this.zCoord + dir.offsetZ;
                if (world.getBlock(x, y, z).getMaterial() != Material.lava || world.rand.nextInt(300) != 0) {
                    continue;
                }
                boolean source = world.getBlockMetadata(x, y, z) == 0;
                world.setBlock(x, y, z, source ? Blocks.obsidian : Blocks.cobblestone);
                break;
            }
        }
        if (this.tempHot >= 100) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (world.rand.nextInt(300) != 0) {
                    continue;
                }
                int x = this.xCoord + dir.offsetX;
                int y = this.yCoord + dir.offsetY;
                int z = this.zCoord + dir.offsetZ;
                Block block = world.getBlock(x, y, z);
                if (block == Blocks.snow_layer) {
                    world.setBlockToAir(x, y, z);
                    break;
                }
                if (block == Blocks.ice || block == Blocks.snow) {
                    if (nether) {
                        world.setBlockToAir(x, y, z);
                    } else {
                        world.setBlock(x, y, z, Blocks.water);
                    }
                    break;
                }
            }
        }
        this.tempHot = hot;
        this.tempCold = cold;
        this.markDirty();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Hot", this.tempHot);
        tag.setInteger("Cold", this.tempCold);
        tag.setInteger("Ticks", this.ticks);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.tempHot = tag.getInteger("Hot");
        this.tempCold = tag.getInteger("Cold");
        this.ticks = tag.getInteger("Ticks");
    }
}
