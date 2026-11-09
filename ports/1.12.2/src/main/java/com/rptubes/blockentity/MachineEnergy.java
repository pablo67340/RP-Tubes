package com.rptubes.blockentity;

import com.rptubes.RPTubesConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Forge Energy (RF-compatible) buffer for machines that used Blutricity in RP2.
 * Thermal, Ender IO, Immersive Engineering, Flux Networks, etc. all speak this.
 * IC2 EU / BuildCraft MJ need a converter — we do not implement those APIs.
 */
public final class MachineEnergy implements IEnergyStorage {
    private final EnergyStorage storage;

    public MachineEnergy() {
        this.storage = new EnergyStorage(RPTubesConfig.energyCapacity, RPTubesConfig.energyCapacity, RPTubesConfig.energyCapacity);
    }

    public boolean pay(boolean mustHaveEnergy) {
        int cost = RPTubesConfig.energyPerOperation;
        if (this.storage.extractEnergy(cost, true) >= cost) {
            this.storage.extractEnergy(cost, false);
            return true;
        }
        return !mustHaveEnergy && !RPTubesConfig.requireEnergy;
    }

    public boolean canAutoExtract() {
        return RPTubesConfig.rfPowersMachines
                && this.storage.getEnergyStored() >= RPTubesConfig.energyPerOperation;
    }

    public void write(NBTTagCompound tag) {
        tag.setInteger("Energy", this.storage.getEnergyStored());
    }

    public void read(NBTTagCompound tag) {
        int stored = tag.getInteger("Energy");
        if (stored > 0) {
            this.storage.receiveEnergy(stored, false);
        }
    }

    public boolean isEnergyCap(Capability<?> cap) {
        return cap == CapabilityEnergy.ENERGY;
    }

    @SuppressWarnings("unchecked")
    public <T> T cast(Capability<T> cap, EnumFacing side) {
        return cap == CapabilityEnergy.ENERGY ? (T) this : null;
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
