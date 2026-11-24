package com.rptubes.blockentity;

import com.rptubes.power.PowerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
        super(ModBlockEntities.THERMOPILE, 1000, 0, 1000);
    }

    @Override
    protected void serverTick() {
        if (++this.ticks > 20) {
            this.ticks = 0;
            this.updateTemps(this.level);
        }
        this.fraction += 0.05 * Math.min(this.tempHot, this.tempCold);
        long whole = (long) this.fraction;
        if (whole > 0) {
            this.fraction -= whole;
            this.buffer.generate(whole);
        }
        this.pushToNeighbours(Long.MAX_VALUE);
    }

    private void updateTemps(Level level) {
        boolean nether = level.dimension() == Level.NETHER;
        int hot = 0;
        int cold = 0;
        for (Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(this.worldPosition.relative(dir));
            if (state.isAir()) {
                if (nether) {
                    hot++;
                } else {
                    cold++;
                }
            } else if (state.is(Blocks.ICE) || state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE)
                    || state.is(Blocks.SNOW_BLOCK)) {
                cold += 100;
            } else if (state.is(Blocks.SNOW)) {
                cold += 50;
            } else if (state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)
                    || state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)) {
                hot += 5;
            } else if (state.is(Blocks.JACK_O_LANTERN)) {
                hot += 3;
            } else if (state.getFluidState().is(FluidTags.WATER)) {
                cold += 25;
            } else if (state.getFluidState().is(FluidTags.LAVA)) {
                hot += 100;
            } else if (state.is(BlockTags.FIRE)) {
                hot += 25;
            }
        }

        if (this.tempHot >= 100 && this.tempCold >= 200) {
            for (Direction dir : Direction.values()) {
                BlockPos target = this.worldPosition.relative(dir);
                BlockState state = level.getBlockState(target);
                if (!state.is(Blocks.LAVA) || level.getRandom().nextInt(300) != 0) {
                    continue;
                }
                level.setBlockAndUpdate(target, state.getFluidState().isSource()
                        ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.BASALT.defaultBlockState());
                break;
            }
        }
        if (this.tempHot >= 100) {
            for (Direction dir : Direction.values()) {
                if (level.getRandom().nextInt(300) != 0) {
                    continue;
                }
                BlockPos target = this.worldPosition.relative(dir);
                BlockState state = level.getBlockState(target);
                if (state.is(Blocks.SNOW)) {
                    level.removeBlock(target, false);
                    break;
                }
                if (state.is(Blocks.ICE) || state.is(Blocks.SNOW_BLOCK)) {
                    level.setBlockAndUpdate(target, nether
                            ? Blocks.AIR.defaultBlockState() : Blocks.WATER.defaultBlockState());
                    break;
                }
            }
        }
        this.tempHot = hot;
        this.tempCold = cold;
        this.setChanged();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("Hot", this.tempHot);
        tag.putInt("Cold", this.tempCold);
        tag.putInt("Ticks", this.ticks);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.tempHot = tag.getInt("Hot");
        this.tempCold = tag.getInt("Cold");
        this.ticks = tag.getInt("Ticks");
    }
}
