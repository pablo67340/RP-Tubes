package com.rptubes.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapts a Fabric Transfer API storage (vanilla chests, other mods) to IItemHandler.
 */
public final class StorageItemHandler implements IItemHandler {
    private final Storage<ItemVariant> storage;

    public StorageItemHandler(Storage<ItemVariant> storage) {
        this.storage = storage;
    }

    private List<StorageView<ItemVariant>> views() {
        List<StorageView<ItemVariant>> list = new ArrayList<>();
        this.storage.iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public int getSlots() {
        int size = views().size();
        return Math.max(size, 1);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        List<StorageView<ItemVariant>> views = views();
        if (slot < 0 || slot >= views.size()) {
            return ItemStack.EMPTY;
        }
        StorageView<ItemVariant> view = views.get(slot);
        if (view.isResourceBlank()) {
            return ItemStack.EMPTY;
        }
        return view.getResource().toStack((int) Math.min(view.getAmount(), Integer.MAX_VALUE));
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = this.storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
            if (!simulate) {
                tx.commit();
            }
            if (inserted <= 0) {
                return stack;
            }
            if (inserted >= stack.getCount()) {
                return ItemStack.EMPTY;
            }
            ItemStack leftover = stack.copy();
            leftover.shrink((int) inserted);
            return leftover;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        List<StorageView<ItemVariant>> views = views();
        if (slot < 0 || slot >= views.size() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        StorageView<ItemVariant> view = views.get(slot);
        if (view.isResourceBlank()) {
            return ItemStack.EMPTY;
        }
        ItemVariant variant = view.getResource();
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = view.extract(variant, amount, tx);
            if (!simulate) {
                tx.commit();
            }
            return extracted <= 0 ? ItemStack.EMPTY : variant.toStack((int) extracted);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        List<StorageView<ItemVariant>> views = views();
        if (slot < 0 || slot >= views.size()) {
            return 64;
        }
        return (int) Math.min(views.get(slot).getCapacity(), Integer.MAX_VALUE);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty();
    }
}
