package com.rptubes.blockentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Java-8 default methods for the IInventory boilerplate that newer
 * versions of the codebase never needed (stack limits, open/close, names).
 */
public interface InvDefaults extends IInventory {
    @Override
    default int getInventoryStackLimit() {
        return 64;
    }

    @Override
    default boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    default void openInventory() {
    }

    @Override
    default void closeInventory() {
    }

    @Override
    default boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    default String getInventoryName() {
        return "";
    }
}
