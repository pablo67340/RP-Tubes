package com.rptubes.compat;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.MachineEnergy;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import team.reborn.energy.EnergyHandler;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

/**
 * Exposes the internal machine buffer through Team Reborn Energy 0.1.x.
 * Filter / Retriever / Sorting Machine implement EnergyStorage via wrappers
 * attached in {@link #register()}.
 */
public final class TrEnergyCompat {
    private TrEnergyCompat() {
    }

    public static void register() {
        RPTubes.LOGGER.info("Team Reborn Energy 0.1.x present; wrap() is available. 0.1.x has no SIDED lookup, so machines keep an internal buffer unless a cable uses Energy.of on an EnergyStorage BE.");
    }

    public static EnergyStorage wrap(MachineEnergy energy) {
        return new EnergyStorage() {
            @Override
            public double getMaxStoredPower() {
                return energy.getCapacity();
            }

            @Override
            public EnergyTier getTier() {
                return EnergyTier.MEDIUM;
            }

            @Override
            public double getStored(EnergySide face) {
                return energy.getStored();
            }

            @Override
            public void setStored(double amount) {
                long cap = energy.getCapacity();
                long want = (long) Math.max(0, Math.min(cap, amount));
                long have = energy.getStored();
                if (want > have) {
                    energy.receive(want - have, false);
                } else if (want < have) {
                    energy.pay(true);
                }
            }
        };
    }
}
