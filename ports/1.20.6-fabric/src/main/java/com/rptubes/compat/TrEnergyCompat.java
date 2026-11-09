package com.rptubes.compat;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.MachineEnergy;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

/** Registers Team Reborn Energy sided storage on Filter / Retriever / Sorting Machine. */
public final class TrEnergyCompat {
    private TrEnergyCompat() {
    }

    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, dir) -> wrap(((FilterBlockEntity) be).getEnergy()),
                ModBlockEntities.FILTER);
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, dir) -> wrap(((FilterBlockEntity) be).getEnergy()),
                ModBlockEntities.RETRIEVER);
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, dir) -> wrap(((SortingMachineBlockEntity) be).getEnergy()),
                ModBlockEntities.SORTING_MACHINE);
        RPTubes.LOGGER.info("Team Reborn Energy attached to Filter, Retriever and Sorting Machine.");
    }

    private static EnergyStorage wrap(MachineEnergy energy) {
        return new EnergyStorage() {
            @Override
            public long insert(long maxAmount, TransactionContext transaction) {
                long accepted = energy.receive(maxAmount, true);
                if (accepted > 0) {
                    transaction.addCloseCallback((t, result) -> {
                        if (result.wasCommitted()) {
                            energy.receive(accepted, false);
                        }
                    });
                }
                return accepted;
            }

            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long getAmount() {
                return energy.getStored();
            }

            @Override
            public long getCapacity() {
                return energy.getCapacity();
            }
        };
    }
}
