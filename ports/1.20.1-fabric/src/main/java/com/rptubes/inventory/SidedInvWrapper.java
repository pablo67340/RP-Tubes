package com.rptubes.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

public class SidedInvWrapper implements IItemHandler {
    private final WorldlyContainer inv;
    private final Direction side;
    private final int[] slots;

    public SidedInvWrapper(WorldlyContainer inv, Direction side) {
        this.inv = inv;
        this.side = side;
        this.slots = inv.getSlotsForFace(side);
    }

    public static IItemHandler[] create(WorldlyContainer inv, Direction... sides) {
        IItemHandler[] handlers = new IItemHandler[sides.length];
        for (int i = 0; i < sides.length; i++) {
            handlers[i] = new SidedInvWrapper(inv, sides[i]);
        }
        return handlers;
    }

    public WorldlyContainer getContainer() {
        return this.inv;
    }

    public int mapSlot(int slot) {
        if (slot < 0 || slot >= this.slots.length) {
            return -1;
        }
        return this.slots[slot];
    }

    @Override
    public int getSlots() {
        return this.slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int real = mapSlot(slot);
        return real < 0 ? ItemStack.EMPTY : this.inv.getItem(real);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int real = mapSlot(slot);
        if (real < 0 || stack.isEmpty() || !this.inv.canPlaceItemThroughFace(real, stack, this.side)) {
            return stack;
        }
        ItemStack existing = this.inv.getItem(real);
        int limit = Math.min(this.inv.getMaxStackSize(), stack.getMaxStackSize());
        if (existing.isEmpty()) {
            int toInsert = Math.min(limit, stack.getCount());
            if (!simulate) {
                ItemStack placed = stack.copy();
                placed.setCount(toInsert);
                this.inv.setItem(real, placed);
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
        int real = mapSlot(slot);
        if (real < 0 || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = this.inv.getItem(real);
        if (existing.isEmpty() || !this.inv.canTakeItemThroughFace(real, existing, this.side)) {
            return ItemStack.EMPTY;
        }
        int toExtract = Math.min(amount, existing.getCount());
        ItemStack result = existing.copy();
        result.setCount(toExtract);
        if (!simulate) {
            existing.shrink(toExtract);
            if (existing.isEmpty()) {
                this.inv.setItem(real, ItemStack.EMPTY);
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
        int real = mapSlot(slot);
        return real >= 0 && this.inv.canPlaceItemThroughFace(real, stack, this.side);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        int real = mapSlot(slot);
        if (real >= 0) {
            this.inv.setItem(real, stack);
            this.inv.setChanged();
        }
    }

    private static ItemStack leftover(ItemStack stack, int inserted) {
        if (inserted >= stack.getCount()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        copy.setCount(stack.getCount() - inserted);
        return copy;
    }
}
