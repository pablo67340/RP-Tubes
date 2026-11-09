package com.rptubes.blockentity;

import com.rptubes.RPTubesConfig;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * NeoForge Energy (RF-compatible) buffer for machines that used Blutricity in RP2.
 */
public final class MachineEnergy implements IEnergyStorage {
    private final EnergyStorage storage;

    public MachineEnergy() {
        int cap = RPTubesConfig.energyCapacity.get();
        this.storage = new EnergyStorage(cap, cap, cap);
    }

    public boolean pay(boolean mustHaveEnergy) {
        int cost = RPTubesConfig.energyPerOperation.get();
        if (this.storage.extractEnergy(cost, true) >= cost) {
            this.storage.extractEnergy(cost, false);
            return true;
        }
        return !mustHaveEnergy && !RPTubesConfig.requireEnergy.get();
    }

    public boolean canAutoExtract() {
        return RPTubesConfig.rfPowersMachines.get()
                && this.storage.getEnergyStored() >= RPTubesConfig.energyPerOperation.get();
    }

    public void write(CompoundTag tag) {
        tag.putInt("Energy", this.storage.getEnergyStored());
    }

    public void read(CompoundTag tag) {
        int stored = tag.getInt("Energy");
        if (stored > 0) {
            this.storage.receiveEnergy(stored, false);
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return this.storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return this.storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return this.storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
