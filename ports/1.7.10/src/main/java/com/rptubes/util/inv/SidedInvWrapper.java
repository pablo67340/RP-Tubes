package com.rptubes.util.inv;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * IItemHandler view over an ISidedInventory for one access side
 * (port of Forge's SidedInvWrapper). Slot indices are remapped through
 * getAccessibleSlotsFromSide, matching the 1.8+ wrapper's behavior.
 */
public class SidedInvWrapper extends InvWrapper {
    private final ISidedInventory sided;
    private final int side;

    public SidedInvWrapper(ISidedInventory inv, ForgeDirection side) {
        super(inv);
        this.sided = inv;
        this.side = side.ordinal();
    }

    private int mapSlot(int slot) {
        int[] slots = this.sided.getAccessibleSlotsFromSide(this.side);
        return slot >= 0 && slot < slots.length ? slots[slot] : -1;
    }

    @Override
    protected boolean canInsert(int slot, ItemStack stack) {
        return this.sided.isItemValidForSlot(slot, stack) && this.sided.canInsertItem(slot, stack, this.side);
    }

    @Override
    protected boolean canExtract(int slot, ItemStack stack) {
        return this.sided.canExtractItem(slot, stack, this.side);
    }

    @Override
    public int getSlots() {
        return this.sided.getAccessibleSlotsFromSide(this.side).length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int real = this.mapSlot(slot);
        return real < 0 ? null : this.sided.getStackInSlot(real);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int real = this.mapSlot(slot);
        return real < 0 ? stack : super.insertItem(real, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int real = this.mapSlot(slot);
        return real < 0 ? null : super.extractItem(real, amount, simulate);
    }
}
