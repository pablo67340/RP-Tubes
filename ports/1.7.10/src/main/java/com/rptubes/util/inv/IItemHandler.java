package com.rptubes.util.inv;

import net.minecraft.item.ItemStack;

/**
 * Minimal recreation of Forge 1.8+'s IItemHandler so the tube routing code
 * ports to 1.7.10 unchanged. Stacks are nullable (1.7.10 convention);
 * insertItem returns the leftover (null = fully inserted).
 */
public interface IItemHandler {
    int getSlots();

    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);
}
