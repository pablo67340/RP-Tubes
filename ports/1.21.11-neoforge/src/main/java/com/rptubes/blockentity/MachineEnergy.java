package com.rptubes.blockentity;

import com.rptubes.RPTubesConfig;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

/**
 * NeoForge Energy buffer for machines that used Blutricity in RP2.
 * Receive-only from cables; {@link #pay} consumes internally.
 */
public final class MachineEnergy extends SimpleEnergyHandler {
    public MachineEnergy() {
        super(RPTubesConfig.energyCapacity.get(), RPTubesConfig.energyCapacity.get(), 0);
    }

    public boolean pay(boolean mustHaveEnergy) {
        int cost = RPTubesConfig.energyPerOperation.get();
        if (this.energy >= cost) {
            this.set(this.energy - cost);
            return true;
        }
        return !mustHaveEnergy && !RPTubesConfig.requireEnergy.get();
    }

    public boolean canAutoExtract() {
        return RPTubesConfig.rfPowersMachines.get()
                && this.energy >= RPTubesConfig.energyPerOperation.get();
    }

    public void write(ValueOutput tag) {
        tag.putInt("Energy", this.energy);
    }

    public void read(ValueInput tag) {
        this.set(tag.getIntOr("Energy", 0));
    }
}
