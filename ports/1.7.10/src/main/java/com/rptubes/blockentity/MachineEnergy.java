package com.rptubes.blockentity;

import net.minecraft.nbt.NBTTagCompound;

/** Receive-only RF buffer for machines that used Blutricity in RP2. */
public final class MachineEnergy {
    public static final int CAPACITY = 8000;
    public static final int COST = 40;

    private int stored;

    public int receive(int maxReceive, boolean simulate) {
        int accepted = Math.max(0, Math.min(maxReceive, CAPACITY - this.stored));
        if (!simulate) {
            this.stored += accepted;
        }
        return accepted;
    }

    /** Spends one operation's worth of energy; false when there is not enough. */
    public boolean pay() {
        if (this.stored < COST) {
            return false;
        }
        this.stored -= COST;
        return true;
    }

    public int getStored() {
        return this.stored;
    }

    public void write(NBTTagCompound tag) {
        tag.setInteger("Energy", this.stored);
    }

    public void read(NBTTagCompound tag) {
        this.stored = Math.max(0, Math.min(CAPACITY, tag.getInteger("Energy")));
    }
}
