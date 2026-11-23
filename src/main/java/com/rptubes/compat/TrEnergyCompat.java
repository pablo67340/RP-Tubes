//? if fabric && <26.1.2 {
/*package com.rptubes.compat;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.MachineEnergy;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.power.EnergyBridge;
import com.rptubes.power.PowerBlockEntity;
import com.rptubes.power.PowerBuffer;
import com.rptubes.registry.ModBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

/^* Registers Team Reborn Energy on the machines and power blocks, and routes EnergyBridge through it. ^/
public final class TrEnergyCompat {
    private TrEnergyCompat() {
    }

    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntities(
                (be, dir) -> ((PowerBlockEntity) be).getBuffer().adapter(PowerStorage::new),
                ModBlockEntities.SOLAR_PANEL, ModBlockEntities.THERMOPILE, ModBlockEntities.BATTERY_BOX);
        EnergyBridge.hook = new EnergyBridge.Hook() {
            @Override
            public long push(Level level, BlockPos pos, Direction dir, long amount) {
                EnergyStorage target = EnergyStorage.SIDED.find(level, pos.relative(dir), dir.getOpposite());
                return target == null ? 0 : insert(target, amount);
            }

            @Override
            public long chargeSlot(Container container, int slot, long amount) {
                EnergyStorage item = slotContext(container, slot).find(EnergyStorage.ITEM);
                return item == null ? 0 : insert(item, amount);
            }

            @Override
            public long drainSlot(Container container, int slot, long amount) {
                EnergyStorage item = slotContext(container, slot).find(EnergyStorage.ITEM);
                if (item == null || !item.supportsExtraction()) {
                    return 0;
                }
                try (Transaction tx = Transaction.openOuter()) {
                    long moved = item.extract(amount, tx);
                    tx.commit();
                    return moved;
                }
            }

            @Override
            public long chargePlayerSlot(Player player, int slot, long amount) {
                ContainerItemContext context = ContainerItemContext.ofPlayerSlot(player,
                        PlayerInventoryStorage.of(player).getSlot(slot));
                EnergyStorage item = context.find(EnergyStorage.ITEM);
                return item == null ? 0 : insert(item, amount);
            }
        };
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, dir) -> wrap(((FilterBlockEntity) be).getEnergy()),
                ModBlockEntities.FILTER);
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, dir) -> wrap(((FilterBlockEntity) be).getEnergy()),
                ModBlockEntities.RETRIEVER);
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, dir) -> wrap(((SortingMachineBlockEntity) be).getEnergy()),
                ModBlockEntities.SORTING_MACHINE);
        RPTubes.LOGGER.info("Team Reborn Energy attached to the machines and power blocks.");
    }

    private static ContainerItemContext slotContext(Container container, int slot) {
        return ContainerItemContext.ofSingleSlot(InventoryStorage.of(container, null).getSlot(slot));
    }

    private static long insert(EnergyStorage target, long amount) {
        if (amount <= 0 || !target.supportsInsertion()) {
            return 0;
        }
        try (Transaction tx = Transaction.openOuter()) {
            long moved = target.insert(amount, tx);
            tx.commit();
            return moved;
        }
    }

    private static final class PowerStorage extends SnapshotParticipant<Long> implements EnergyStorage {
        private final PowerBuffer buffer;

        PowerStorage(PowerBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public boolean supportsInsertion() {
            return this.buffer.canReceive();
        }

        @Override
        public boolean supportsExtraction() {
            return this.buffer.canExtract();
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            long accepted = this.buffer.receive(maxAmount, true);
            if (accepted > 0) {
                this.updateSnapshots(transaction);
                this.buffer.setStored(this.buffer.getStored() + accepted);
            }
            return accepted;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            long taken = this.buffer.extract(maxAmount, true);
            if (taken > 0) {
                this.updateSnapshots(transaction);
                this.buffer.setStored(this.buffer.getStored() - taken);
            }
            return taken;
        }

        @Override
        public long getAmount() {
            return this.buffer.getStored();
        }

        @Override
        public long getCapacity() {
            return this.buffer.getCapacity();
        }

        @Override
        protected Long createSnapshot() {
            return this.buffer.getStored();
        }

        @Override
        protected void readSnapshot(Long snapshot) {
            this.buffer.setStored(snapshot);
        }

        @Override
        protected void onFinalCommit() {
            this.buffer.markChanged();
        }
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
*///?}
