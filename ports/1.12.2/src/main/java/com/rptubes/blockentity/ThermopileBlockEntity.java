package com.rptubes.blockentity;

import com.rptubes.power.PowerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Port of TileThermopile: output is 0.05 FE/t per degree of min(hot, cold), with the six
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
            this.updateTemps(this.world);
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
        boolean nether = world.provider.isNether();
        int hot = 0;
        int cold = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos at = this.pos.offset(dir);
            IBlockState state = world.getBlockState(at);
            Block block = state.getBlock();
            Material material = state.getMaterial();
            if (block.isAir(state, world, at)) {
                if (nether) {
                    hot++;
                } else {
                    cold++;
                }
            } else if (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.SNOW) {
                cold += 100;
            } else if (block == Blocks.SNOW_LAYER) {
                cold += 50;
            } else if (block == Blocks.TORCH) {
                hot += 5;
            } else if (block == Blocks.LIT_PUMPKIN) {
                hot += 3;
            } else if (material == Material.WATER) {
                cold += 25;
            } else if (material == Material.LAVA) {
                hot += 100;
            } else if (block == Blocks.FIRE) {
                hot += 25;
            }
        }

        if (this.tempHot >= 100 && this.tempCold >= 200) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                BlockPos target = this.pos.offset(dir);
                IBlockState state = world.getBlockState(target);
                if (state.getMaterial() != Material.LAVA || world.rand.nextInt(300) != 0) {
                    continue;
                }
                boolean source = state.getBlock() instanceof BlockLiquid && state.getValue(BlockLiquid.LEVEL) == 0;
                world.setBlockState(target, source
                        ? Blocks.OBSIDIAN.getDefaultState() : Blocks.COBBLESTONE.getDefaultState());
                break;
            }
        }
        if (this.tempHot >= 100) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                if (world.rand.nextInt(300) != 0) {
                    continue;
                }
                BlockPos target = this.pos.offset(dir);
                Block block = world.getBlockState(target).getBlock();
                if (block == Blocks.SNOW_LAYER) {
                    world.setBlockToAir(target);
                    break;
                }
                if (block == Blocks.ICE || block == Blocks.SNOW) {
                    world.setBlockState(target, nether
                            ? Blocks.AIR.getDefaultState() : Blocks.WATER.getDefaultState());
                    break;
                }
            }
        }
        this.tempHot = hot;
        this.tempCold = cold;
        this.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Hot", this.tempHot);
        tag.setInteger("Cold", this.tempCold);
        tag.setInteger("Ticks", this.ticks);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.tempHot = tag.getInteger("Hot");
        this.tempCold = tag.getInteger("Cold");
        this.ticks = tag.getInteger("Ticks");
    }
}
