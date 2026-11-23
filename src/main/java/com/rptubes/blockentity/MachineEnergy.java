package com.rptubes.blockentity;

import com.rptubes.RPTubesConfig;
//? if fabric && <1.20.6 {
/*import net.minecraft.nbt.CompoundTag;

/^*
 * Internal energy buffer for machines that used Blutricity in RP2.
 * Team Reborn Energy is attached separately via {@link com.rptubes.compat.TrEnergyCompat}.
 ^/
public final class MachineEnergy {
    private long stored;
*///?} else if fabric && <1.21.11 {
/*import net.minecraft.nbt.CompoundTag;

/^*
 * Internal energy buffer for machines that used Blutricity in RP2.
 * Team Reborn Energy is attached separately via TrEnergyCompat.
 ^/
public final class MachineEnergy {
    private long stored;
*///?} else if neoforge && <1.21.11 {
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
//?} else if fabric {

/*/^*
 * Internal energy buffer for machines that used Blutricity in RP2.
 * Team Reborn Energy is attached separately via TrEnergyCompat.
 ^/
public final class MachineEnergy {
    private long stored;
*///?} else if forge && >=1.21.11 {
/*import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/^*
 * Forge Energy (RF-compatible) buffer for machines that used Blutricity in RP2.
 ^/
public final class MachineEnergy implements IEnergyStorage {
    private final EnergyStorage storage;
    private LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> this);

    public MachineEnergy() {
        int cap = RPTubesConfig.energyCapacity.get();
        this.storage = new EnergyStorage(cap, cap, cap);
    }
*///?} else if >=1.21.11 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

/^*
 * NeoForge Energy buffer for machines that used Blutricity in RP2.
 * Receive-only from cables; {@link #pay} consumes internally.
 ^/
public final class MachineEnergy extends SimpleEnergyHandler {
    public MachineEnergy() {
        super(RPTubesConfig.energyCapacity.get(), RPTubesConfig.energyCapacity.get(), 0);
    }
*///?} else {
/*import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/^*
 * Forge Energy (RF-compatible) buffer for machines that used Blutricity in RP2.
 ^/
public final class MachineEnergy implements IEnergyStorage {
    private final EnergyStorage storage;
    private LazyOptional<IEnergyStorage> optional = LazyOptional.of(() -> this);

    public MachineEnergy() {
        int cap = RPTubesConfig.energyCapacity.get();
        this.storage = new EnergyStorage(cap, cap, cap);
    }
*///?}

    public boolean pay(boolean mustHaveEnergy) {
        //? if fabric {
        /*int cost = RPTubesConfig.energyPerOperation;
        if (this.stored >= cost) {
            this.stored -= cost;
        *///?} else if neoforge && >=1.21.11 {
        /*int cost = RPTubesConfig.energyPerOperation.get();
        if (this.energy >= cost) {
            this.set(this.energy - cost);
        *///?} else {
        int cost = RPTubesConfig.energyPerOperation.get();
        if (this.storage.extractEnergy(cost, true) >= cost) {
            this.storage.extractEnergy(cost, false);
        //?}
            return true;
        }
        //? if forgelike {
        return !mustHaveEnergy && !RPTubesConfig.requireEnergy.get();
        //?} else {
        /*return !mustHaveEnergy && !RPTubesConfig.requireEnergy;
        *///?}
    }

    public boolean canAutoExtract() {
        //? if fabric {
        /*return RPTubesConfig.rfPowersMachines
                && this.stored >= RPTubesConfig.energyPerOperation;
        *///?} else if neoforge && >=1.21.11 {
        /*return RPTubesConfig.rfPowersMachines.get()
                && this.energy >= RPTubesConfig.energyPerOperation.get();
        *///?} else {
        return RPTubesConfig.rfPowersMachines.get()
                && this.storage.getEnergyStored() >= RPTubesConfig.energyPerOperation.get();
        //?}
    }

    //? if fabric {
    /*public long receive(long maxReceive, boolean simulate) {
        if (!RPTubesConfig.rfPowersMachines || maxReceive <= 0) {
            return 0;
        }
        long room = Math.max(0, (long) RPTubesConfig.energyCapacity - this.stored);
        long added = Math.min(room, maxReceive);
        if (!simulate) {
            this.stored += added;
        }
        return added;
    *///?} else if <1.21.11 {
    public void write(CompoundTag tag) {
        tag.putInt("Energy", this.storage.getEnergyStored());
    //?} else if forge {
    /*public void write(ValueOutput tag) {
        tag.putInt("Energy", this.storage.getEnergyStored());
    *///?} else {
    /*public void write(ValueOutput tag) {
        tag.putInt("Energy", this.energy);
    *///?}
    }

    //? if fabric && >=1.21.11 && <26.1.2 {
    /*public long getStored() {
        return this.stored;
    }

    public long getCapacity() {
        return RPTubesConfig.energyCapacity;
    }

    public void write(net.minecraft.world.level.storage.ValueOutput tag) {
        tag.putLong("Energy", this.stored);
    }

    public void read(net.minecraft.world.level.storage.ValueInput tag) {
        this.stored = tag.getLongOr("Energy", 0L);
    }

    *///?} else if neoforge && <1.21.11 {
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
    //?} else if fabric && >=1.21.11 {
    /*public long getStored() {
        return this.stored;
    }

    public long getCapacity() {
        return RPTubesConfig.energyCapacity;
    }

    public void write(net.minecraft.world.level.storage.ValueOutput tag) {
        tag.putLong("Energy", this.stored);
    }

    public void read(net.minecraft.world.level.storage.ValueInput tag) {
        this.stored = tag.getLongOr("Energy", 0L);
    }
    *///?} else if fabric {
    /*public long getStored() {
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
    *///?} else if forge && >=1.21.11 {
    /*public void read(ValueInput tag) {
        int stored = tag.getIntOr("Energy", 0);
        if (stored > 0) {
            this.storage.receiveEnergy(stored, false);
        }
    }

    public boolean isEnergyCap(Capability<?> cap) {
        return cap == ForgeCapabilities.ENERGY;
    }

    public <T> LazyOptional<T> cast(Capability<T> cap, Direction side) {
        return cap == ForgeCapabilities.ENERGY ? this.optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        this.optional.invalidate();
    }

    public void revive() {
        this.optional = LazyOptional.of(() -> this);
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
    *///?} else if >=1.21.11 {
    /*public void read(ValueInput tag) {
        this.set(tag.getIntOr("Energy", 0));
    }
    *///?} else {
    /*public void read(CompoundTag tag) {
        int stored = tag.getInt("Energy");
        if (stored > 0) {
            this.storage.receiveEnergy(stored, false);
        }
    }

    public boolean isEnergyCap(Capability<?> cap) {
        return cap == ForgeCapabilities.ENERGY;
    }

    public <T> LazyOptional<T> cast(Capability<T> cap, Direction side) {
        return cap == ForgeCapabilities.ENERGY ? this.optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        this.optional.invalidate();
    }

    public void revive() {
        this.optional = LazyOptional.of(() -> this);
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
    *///?}
}
