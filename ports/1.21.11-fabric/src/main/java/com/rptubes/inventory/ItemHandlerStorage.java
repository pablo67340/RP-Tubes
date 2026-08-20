package com.rptubes.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Exposes an IItemHandler as Fabric Transfer API storage for hoppers and other mods.
 */
public final class ItemHandlerStorage extends SnapshotParticipant<ItemStack[]> implements Storage<ItemVariant> {
    private final IItemHandler handler;

    private ItemHandlerStorage(IItemHandler handler) {
        this.handler = handler;
    }

    @Nullable
    public static Storage<ItemVariant> of(@Nullable IItemHandler handler) {
        return handler == null ? null : new ItemHandlerStorage(handler);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        ItemStack leftover = ItemHandlerHelper.insertItem(this.handler, resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE)), false);
        return maxAmount - leftover.getCount();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        long remaining = maxAmount;
        for (int i = 0; i < this.handler.getSlots() && remaining > 0; i++) {
            ItemStack inSlot = this.handler.getStackInSlot(i);
            if (inSlot.isEmpty() || !resource.matches(inSlot)) {
                continue;
            }
            ItemStack took = this.handler.extractItem(i, (int) Math.min(remaining, Integer.MAX_VALUE), false);
            remaining -= took.getCount();
        }
        return maxAmount - remaining;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return new Iterator<>() {
            int slot = 0;

            @Override
            public boolean hasNext() {
                return this.slot < handler.getSlots();
            }

            @Override
            public StorageView<ItemVariant> next() {
                return new SlotView(this.slot++);
            }
        };
    }

    @Override
    protected ItemStack[] createSnapshot() {
        ItemStack[] snap = new ItemStack[this.handler.getSlots()];
        for (int i = 0; i < snap.length; i++) {
            snap[i] = this.handler.getStackInSlot(i).copy();
        }
        return snap;
    }

    @Override
    protected void readSnapshot(ItemStack[] snapshot) {
        for (int i = 0; i < snapshot.length && i < this.handler.getSlots(); i++) {
            setSlot(i, snapshot[i].copy());
        }
    }

    private void setSlot(int slot, ItemStack stack) {
        if (this.handler instanceof InvWrapper inv) {
            inv.setStackInSlot(slot, stack);
            return;
        }
        if (this.handler instanceof SidedInvWrapper sided) {
            sided.setStackInSlot(slot, stack);
            return;
        }
        ItemStack current = this.handler.getStackInSlot(slot);
        if (!current.isEmpty()) {
            this.handler.extractItem(slot, current.getCount(), false);
        }
        if (!stack.isEmpty()) {
            this.handler.insertItem(slot, stack, false);
        }
    }

    private final class SlotView implements SingleSlotStorage<ItemVariant> {
        private final int slot;

        private SlotView(int slot) {
            this.slot = slot;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            updateSnapshots(transaction);
            ItemStack leftover = handler.insertItem(this.slot, resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE)), false);
            return maxAmount - leftover.getCount();
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            ItemStack inSlot = handler.getStackInSlot(this.slot);
            if (inSlot.isEmpty() || !resource.matches(inSlot) || maxAmount <= 0) {
                return 0;
            }
            updateSnapshots(transaction);
            ItemStack took = handler.extractItem(this.slot, (int) Math.min(maxAmount, Integer.MAX_VALUE), false);
            return took.getCount();
        }

        @Override
        public boolean isResourceBlank() {
            return handler.getStackInSlot(this.slot).isEmpty();
        }

        @Override
        public ItemVariant getResource() {
            ItemStack stack = handler.getStackInSlot(this.slot);
            return stack.isEmpty() ? ItemVariant.blank() : ItemVariant.of(stack);
        }

        @Override
        public long getAmount() {
            return handler.getStackInSlot(this.slot).getCount();
        }

        @Override
        public long getCapacity() {
            return handler.getSlotLimit(this.slot);
        }
    }
}
