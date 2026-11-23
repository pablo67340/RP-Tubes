package com.rptubes.blockentity;

import com.rptubes.power.PowerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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

    public ThermopileBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.THERMOPILE.get(), pos, state, 1000, 0, 1000);
        //?} else {
        /*super(ModBlockEntities.THERMOPILE, pos, state, 1000, 0, 1000);
        *///?}
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThermopileBlockEntity be) {
        if (++be.ticks > 20) {
            be.ticks = 0;
            be.updateTemps(level);
        }
        be.fraction += 0.05 * Math.min(be.tempHot, be.tempCold);
        long whole = (long) be.fraction;
        if (whole > 0) {
            be.fraction -= whole;
            be.buffer.generate(whole);
        }
        be.pushToNeighbours(Long.MAX_VALUE);
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
                    || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.POWDER_SNOW)) {
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
    //? if <1.20.6 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    *///?} else if <1.21.11 {
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    //?} else {
    /*protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
    *///?}
        tag.putInt("Hot", this.tempHot);
        tag.putInt("Cold", this.tempCold);
        tag.putInt("Ticks", this.ticks);
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.tempHot = tag.getInt("Hot");
        this.tempCold = tag.getInt("Cold");
        this.ticks = tag.getInt("Ticks");
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.tempHot = tag.getInt("Hot");
        this.tempCold = tag.getInt("Cold");
        this.ticks = tag.getInt("Ticks");
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.tempHot = tag.getIntOr("Hot", 0);
        this.tempCold = tag.getIntOr("Cold", 0);
        this.ticks = tag.getIntOr("Ticks", 0);
    *///?}
    }
}
