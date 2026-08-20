package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/** Redstone tube — same item routing as pneumatic, plus a cached redstone power level. */
public class RedstoneTubeBlockEntity extends PneumaticTubeBlockEntity {
    private int power;

    public RedstoneTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REDSTONE_TUBE, pos, state);
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Power", this.power);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.power = tag.getInt("Power");
    }
}
