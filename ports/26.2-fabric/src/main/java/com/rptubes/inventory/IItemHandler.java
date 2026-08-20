package com.rptubes.inventory;

import net.minecraft.world.item.ItemStack;

/**
 * Slot-based item handler matching Forge's IItemHandler so MachineLib / tube
 * routing keep the same insert/extract rules on Fabric.
 */
public interface IItemHandler {
    int getSlots();

    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);

    boolean isItemValid(int slot, ItemStack stack);
}
