package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/** Redstone tube — same item routing as pneumatic, plus a cached redstone power level. */
public class RedstoneTubeBlockEntity extends PneumaticTubeBlockEntity {
    private int power;

    public RedstoneTubeBlockEntity() {
        super(ModBlockEntities.REDSTONE_TUBE);
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
        this.setChanged();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("Power", this.power);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.power = tag.getInt("Power");
    }
}
