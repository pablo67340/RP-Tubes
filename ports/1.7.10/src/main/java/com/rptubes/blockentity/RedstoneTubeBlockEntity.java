package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import com.rptubes.util.BlockPos;
import net.minecraft.nbt.NBTTagCompound;

/** Redstone tube — same item routing as pneumatic, plus a cached redstone power world. */
public class RedstoneTubeBlockEntity extends PneumaticTubeBlockEntity {
    private int power;

    public RedstoneTubeBlockEntity() {
        super();
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
        this.markDirty();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Power", this.power);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.power = tag.getInteger("Power");
    }
}
