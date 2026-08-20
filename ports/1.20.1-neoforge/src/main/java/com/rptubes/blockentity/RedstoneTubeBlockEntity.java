package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/** Redstone tube — same item routing as pneumatic, plus a cached redstone power level. */
public class RedstoneTubeBlockEntity extends PneumaticTubeBlockEntity {
    private int power;

    public RedstoneTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REDSTONE_TUBE.get(), pos, state);
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Power", this.power);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.power = tag.getInt("Power");
    }
}
