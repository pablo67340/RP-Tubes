package com.rptubes.registry;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Presents a legacy {@link IItemHandler} as a 26.2 {@link ResourceHandler}.
 * Snapshots the backing item list so aborted transactions can roll back.
 */
public final class LegacyItemResourceHandler extends SnapshotJournal<ItemStack[]> implements ResourceHandler<ItemResource> {
    private final IItemHandler handler;
    private final NonNullList<ItemStack> backing;

    public LegacyItemResourceHandler(IItemHandler handler, NonNullList<ItemStack> backing) {
        this.handler = handler;
        this.backing = backing;
    }

    @Override
    protected ItemStack[] createSnapshot() {
        ItemStack[] snap = new ItemStack[this.backing.size()];
        for (int i = 0; i < snap.length; i++) {
            snap[i] = this.backing.get(i).copy();
        }
        return snap;
    }

    @Override
    protected void revertToSnapshot(ItemStack[] snapshot) {
        for (int i = 0; i < snapshot.length && i < this.backing.size(); i++) {
            this.backing.set(i, snapshot[i]);
        }
    }

    @Override
    public int size() {
        return this.handler.getSlots();
    }

    @Override
    public ItemResource getResource(int index) {
        return ItemResource.of(this.handler.getStackInSlot(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        return this.handler.getStackInSlot(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return this.handler.getSlotLimit(index);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return this.handler.isItemValid(index, resource.toStack());
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        this.updateSnapshots(transaction);
        ItemStack leftover = this.handler.insertItem(index, resource.toStack(amount), false);
        return amount - leftover.getCount();
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        ItemStack current = this.handler.getStackInSlot(index);
        if (current.isEmpty() || !resource.equals(ItemResource.of(current))) {
            return 0;
        }
        this.updateSnapshots(transaction);
        return this.handler.extractItem(index, amount, false).getCount();
    }
}
