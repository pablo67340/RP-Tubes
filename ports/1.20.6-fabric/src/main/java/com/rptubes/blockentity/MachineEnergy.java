package com.rptubes.blockentity;

import com.rptubes.RPTubesConfig;
import net.minecraft.nbt.CompoundTag;

/**
 * Internal energy buffer for machines that used Blutricity in RP2.
 * Team Reborn Energy is attached separately via TrEnergyCompat.
 */
public final class MachineEnergy {
    private long stored;

    public boolean pay(boolean mustHaveEnergy) {
        int cost = RPTubesConfig.energyPerOperation;
        if (this.stored >= cost) {
            this.stored -= cost;
            return true;
        }
        return !mustHaveEnergy && !RPTubesConfig.requireEnergy;
    }

    public boolean canAutoExtract() {
        return RPTubesConfig.rfPowersMachines
                && this.stored >= RPTubesConfig.energyPerOperation;
    }

    public long receive(long maxReceive, boolean simulate) {
        if (!RPTubesConfig.rfPowersMachines || maxReceive <= 0) {
            return 0;
        }
        long room = Math.max(0, (long) RPTubesConfig.energyCapacity - this.stored);
        long added = Math.min(room, maxReceive);
        if (!simulate) {
            this.stored += added;
        }
        return added;
    }

    public long getStored() {
        return this.stored;
    }

    public long getCapacity() {
        return RPTubesConfig.energyCapacity;
    }

    public void write(CompoundTag tag) {
        tag.putLong("Energy", this.stored);
    }

    public void read(CompoundTag tag) {
        this.stored = tag.getLong("Energy");
    }
}
