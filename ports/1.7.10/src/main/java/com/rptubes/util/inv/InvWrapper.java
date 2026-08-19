package com.rptubes.util.inv;

import com.rptubes.util.Stacks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** IItemHandler view over a plain IInventory (port of Forge's InvWrapper). */
public class InvWrapper implements IItemHandler {
    protected final IInventory inv;

    public InvWrapper(IInventory inv) {
        this.inv = inv;
    }

    @Override
    public int getSlots() {
        return this.inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inv.getStackInSlot(slot);
    }

    protected boolean canInsert(int slot, ItemStack stack) {
        return this.inv.isItemValidForSlot(slot, stack);
    }

    protected boolean canExtract(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (Stacks.isEmpty(stack)) {
            return null;
        }
        if (!this.canInsert(slot, stack)) {
            return stack;
        }
        ItemStack existing = this.inv.getStackInSlot(slot);
        int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
        if (Stacks.isEmpty(existing)) {
            int toMove = Math.min(limit, stack.stackSize);
            if (toMove <= 0) {
                return stack;
            }
            if (!simulate) {
                ItemStack placed = stack.copy();
                placed.stackSize = toMove;
                this.inv.setInventorySlotContents(slot, placed);
                this.inv.markDirty();
            }
            if (toMove >= stack.stackSize) {
                return null;
            }
            ItemStack rest = stack.copy();
            rest.stackSize = stack.stackSize - toMove;
            return rest;
        }
        if (!existing.isItemEqual(stack) || !ItemStack.areItemStackTagsEqual(existing, stack)) {
            return stack;
        }
        int space = limit - existing.stackSize;
        if (space <= 0) {
            return stack;
        }
        int toMove = Math.min(space, stack.stackSize);
        if (!simulate) {
            existing.stackSize += toMove;
            this.inv.markDirty();
        }
        if (toMove >= stack.stackSize) {
            return null;
        }
        ItemStack rest = stack.copy();
        rest.stackSize = stack.stackSize - toMove;
        return rest;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return null;
        }
        ItemStack existing = this.inv.getStackInSlot(slot);
        if (Stacks.isEmpty(existing) || !this.canExtract(slot, existing)) {
            return null;
        }
        int toMove = Math.min(amount, existing.stackSize);
        ItemStack taken = existing.copy();
        taken.stackSize = toMove;
        if (!simulate) {
            existing.stackSize -= toMove;
            if (existing.stackSize <= 0) {
                this.inv.setInventorySlotContents(slot, null);
            }
            this.inv.markDirty();
        }
        return taken;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.inv.getInventoryStackLimit();
    }
}
