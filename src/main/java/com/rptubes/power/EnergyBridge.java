package com.rptubes.power;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
//? if neoforge && <1.21.11 {
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/** Moves FE between RP Tubes power blocks and the loader's energy API. */
public final class EnergyBridge {
    private EnergyBridge() {
    }

    public static IEnergyStorage storage(PowerBuffer buffer) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return (int) buffer.receive(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return (int) buffer.extract(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return clamp(buffer.getStored());
            }

            @Override
            public int getMaxEnergyStored() {
                return clamp(buffer.getCapacity());
            }

            @Override
            public boolean canExtract() {
                return buffer.canExtract();
            }

            @Override
            public boolean canReceive() {
                return buffer.canReceive();
            }
        };
    }

    public static long push(Level level, BlockPos pos, Direction dir, long amount) {
        IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.getOpposite());
        if (target == null || !target.canReceive()) {
            return 0;
        }
        return target.receiveEnergy(clamp(amount), false);
    }

    public static long chargeSlot(Container container, int slot, long amount) {
        IEnergyStorage item = container.getItem(slot).getCapability(Capabilities.EnergyStorage.ITEM);
        if (item == null || !item.canReceive()) {
            return 0;
        }
        int moved = item.receiveEnergy(clamp(amount), false);
        if (moved > 0) {
            container.setChanged();
        }
        return moved;
    }

    public static long drainSlot(Container container, int slot, long amount) {
        IEnergyStorage item = container.getItem(slot).getCapability(Capabilities.EnergyStorage.ITEM);
        if (item == null || !item.canExtract()) {
            return 0;
        }
        int moved = item.extractEnergy(clamp(amount), false);
        if (moved > 0) {
            container.setChanged();
        }
        return moved;
    }

    public static long chargePlayerSlot(Player player, int slot, long amount) {
        return chargeSlot(player.getInventory(), slot, amount);
    }

    private static int clamp(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, value));
    }
}
//?} else if neoforge {
/*import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/^* Moves FE between RP Tubes power blocks and the loader's energy API. ^/
public final class EnergyBridge {
    private EnergyBridge() {
    }

    public static EnergyHandler handler(PowerBuffer buffer) {
        return new BufferHandler(buffer);
    }

    public static long push(Level level, BlockPos pos, Direction dir, long amount) {
        EnergyHandler target = level.getCapability(Capabilities.Energy.BLOCK, pos.relative(dir), dir.getOpposite());
        return target == null ? 0 : insert(target, amount);
    }

    public static long chargeSlot(Container container, int slot, long amount) {
        if (container.getItem(slot).isEmpty()) {
            return 0;
        }
        EnergyHandler item = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(container), slot)
                .getCapability(Capabilities.Energy.ITEM);
        return item == null ? 0 : insert(item, amount);
    }

    public static long drainSlot(Container container, int slot, long amount) {
        if (container.getItem(slot).isEmpty()) {
            return 0;
        }
        EnergyHandler item = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(container), slot)
                .getCapability(Capabilities.Energy.ITEM);
        if (item == null || amount <= 0) {
            return 0;
        }
        try (Transaction tx = Transaction.openRoot()) {
            int moved = item.extract(clamp(amount), tx);
            tx.commit();
            return moved;
        }
    }

    public static long chargePlayerSlot(Player player, int slot, long amount) {
        if (player.getInventory().getItem(slot).isEmpty()) {
            return 0;
        }
        EnergyHandler item = ItemAccess.forPlayerSlot(player, slot).getCapability(Capabilities.Energy.ITEM);
        return item == null ? 0 : insert(item, amount);
    }

    private static long insert(EnergyHandler target, long amount) {
        if (amount <= 0) {
            return 0;
        }
        try (Transaction tx = Transaction.openRoot()) {
            int moved = target.insert(clamp(amount), tx);
            tx.commit();
            return moved;
        }
    }

    private static int clamp(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, value));
    }

    private static final class BufferHandler extends SnapshotJournal<Long> implements EnergyHandler {
        private final PowerBuffer buffer;

        BufferHandler(PowerBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public long getAmountAsLong() {
            return this.buffer.getStored();
        }

        @Override
        public long getCapacityAsLong() {
            return this.buffer.getCapacity();
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            int accepted = (int) this.buffer.receive(amount, true);
            if (accepted > 0) {
                this.updateSnapshots(transaction);
                this.buffer.setStored(this.buffer.getStored() + accepted);
            }
            return accepted;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            int taken = (int) this.buffer.extract(amount, true);
            if (taken > 0) {
                this.updateSnapshots(transaction);
                this.buffer.setStored(this.buffer.getStored() - taken);
            }
            return taken;
        }

        @Override
        protected Long createSnapshot() {
            return this.buffer.getStored();
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            this.buffer.setStored(snapshot);
        }

        @Override
        protected void onRootCommit(Long originalState) {
            this.buffer.markChanged();
        }
    }
}
*///?} else if forge {
/*import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

/^* Moves FE between RP Tubes power blocks and the loader's energy API. ^/
public final class EnergyBridge {
    private EnergyBridge() {
    }

    public static IEnergyStorage storage(PowerBuffer buffer) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return (int) buffer.receive(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return (int) buffer.extract(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return clamp(buffer.getStored());
            }

            @Override
            public int getMaxEnergyStored() {
                return clamp(buffer.getCapacity());
            }

            @Override
            public boolean canExtract() {
                return buffer.canExtract();
            }

            @Override
            public boolean canReceive() {
                return buffer.canReceive();
            }
        };
    }

    public static long push(Level level, BlockPos pos, Direction dir, long amount) {
        BlockEntity be = level.getBlockEntity(pos.relative(dir));
        if (be == null) {
            return 0;
        }
        IEnergyStorage target = be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
        if (target == null || !target.canReceive()) {
            return 0;
        }
        return target.receiveEnergy(clamp(amount), false);
    }

    public static long chargeSlot(Container container, int slot, long amount) {
        IEnergyStorage item = container.getItem(slot).getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (item == null || !item.canReceive()) {
            return 0;
        }
        int moved = item.receiveEnergy(clamp(amount), false);
        if (moved > 0) {
            container.setChanged();
        }
        return moved;
    }

    public static long drainSlot(Container container, int slot, long amount) {
        IEnergyStorage item = container.getItem(slot).getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (item == null || !item.canExtract()) {
            return 0;
        }
        int moved = item.extractEnergy(clamp(amount), false);
        if (moved > 0) {
            container.setChanged();
        }
        return moved;
    }

    public static long chargePlayerSlot(Player player, int slot, long amount) {
        return chargeSlot(player.getInventory(), slot, amount);
    }

    private static int clamp(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, value));
    }
}
*///?} else {
/*import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.blockentity.AcceleratorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

/^*
 * Moves FE between RP Tubes power blocks and Team Reborn Energy. Without TR (or on
 * 26.x, where it has no release), energy only flows between RP Tubes blocks.
 ^/
public final class EnergyBridge {
    /^* Installed by TrEnergyCompat when Team Reborn Energy is present. ^/
    public static Hook hook;

    private EnergyBridge() {
    }

    public interface Hook {
        long push(Level level, BlockPos pos, Direction dir, long amount);

        long chargeSlot(Container container, int slot, long amount);

        long drainSlot(Container container, int slot, long amount);

        long chargePlayerSlot(Player player, int slot, long amount);
    }

    public static long push(Level level, BlockPos pos, Direction dir, long amount) {
        if (hook != null) {
            return hook.push(level, pos, dir, amount);
        }
        BlockEntity be = level.getBlockEntity(pos.relative(dir));
        if (be instanceof PowerBlockEntity power) {
            return power.getBuffer().receive(amount, false);
        }
        if (be instanceof FilterBlockEntity filter) {
            return filter.getEnergy().receive(amount, false);
        }
        if (be instanceof SortingMachineBlockEntity sorter) {
            return sorter.getEnergy().receive(amount, false);
        }
        if (be instanceof AcceleratorBlockEntity accelerator) {
            return accelerator.getEnergy().receive(amount, false);
        }
        return 0;
    }

    public static long chargeSlot(Container container, int slot, long amount) {
        return hook == null ? 0 : hook.chargeSlot(container, slot, amount);
    }

    public static long drainSlot(Container container, int slot, long amount) {
        return hook == null ? 0 : hook.drainSlot(container, slot, amount);
    }

    public static long chargePlayerSlot(Player player, int slot, long amount) {
        return hook == null ? 0 : hook.chargePlayerSlot(player, slot, amount);
    }
}
*///?}
