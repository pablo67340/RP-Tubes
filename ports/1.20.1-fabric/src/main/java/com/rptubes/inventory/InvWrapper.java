package com.rptubes.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class InvWrapper implements IItemHandler {
    protected final Container inv;

    public InvWrapper(Container inv) {
        this.inv = inv;
    }

    public Container getContainer() {
        return this.inv;
    }

    @Override
    public int getSlots() {
        return this.inv.getContainerSize();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inv.getItem(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }
        ItemStack existing = this.inv.getItem(slot);
        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        if (existing.isEmpty()) {
            int toInsert = Math.min(limit, stack.getCount());
            if (!simulate) {
                this.inv.setItem(slot, copyWithCount(stack, toInsert));
                this.inv.setChanged();
            }
            return leftover(stack, toInsert);
        }
        if (!ItemStack.isSameItemSameTags(existing, stack)) {
            return stack;
        }
        int space = limit - existing.getCount();
        if (space <= 0) {
            return stack;
        }
        int toInsert = Math.min(space, stack.getCount());
        if (!simulate) {
            existing.grow(toInsert);
            this.inv.setChanged();
        }
        return leftover(stack, toInsert);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = this.inv.getItem(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int toExtract = Math.min(amount, existing.getCount());
        ItemStack result = existing.copy();
        result.setCount(toExtract);
        if (!simulate) {
            existing.shrink(toExtract);
            if (existing.isEmpty()) {
                this.inv.setItem(slot, ItemStack.EMPTY);
            }
            this.inv.setChanged();
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.inv.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.inv.canPlaceItem(slot, stack);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        this.inv.setItem(slot, stack);
        this.inv.setChanged();
    }

    private static ItemStack leftover(ItemStack stack, int inserted) {
        if (inserted >= stack.getCount()) {
            return ItemStack.EMPTY;
        }
        return copyWithCount(stack, stack.getCount() - inserted);
    }

    private static ItemStack copyWithCount(ItemStack stack, int count) {
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }
}
