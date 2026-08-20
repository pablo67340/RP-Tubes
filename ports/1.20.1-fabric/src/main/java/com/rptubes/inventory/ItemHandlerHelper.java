package com.rptubes.inventory;

import net.minecraft.world.item.ItemStack;

public final class ItemHandlerHelper {
    private ItemHandlerHelper() {
    }

    public static ItemStack insertItem(IItemHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty()) {
            return stack;
        }
        for (int i = 0; i < dest.getSlots(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public static boolean canItemStacksStack(ItemStack a, ItemStack b) {
        return ItemStack.isSameItemSameTags(a, b);
    }
}
