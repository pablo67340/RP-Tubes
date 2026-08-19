package com.rptubes.blockentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Java-8 default methods for the 1.12 IInventory boilerplate that newer
 * versions of the codebase never needed (fields, stack limits, open/close).
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
    default int getField(int id) {
        return 0;
    }

    @Override
    default void setField(int id, int value) {
    }

    @Override
    default int getFieldCount() {
        return 0;
    }

    @Override
    default void openInventory(EntityPlayer player) {
    }

    @Override
    default void closeInventory(EntityPlayer player) {
    }

    @Override
    default boolean hasCustomName() {
        return false;
    }

    @Override
    default String getName() {
        return "";
    }
}
