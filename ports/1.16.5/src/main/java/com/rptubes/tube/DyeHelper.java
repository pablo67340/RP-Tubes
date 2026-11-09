package com.rptubes.tube;

import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;

/** Resolve a held stack to a tube paint value (1-16), or -1 if it is not a dye. */
public final class DyeHelper {
    private DyeHelper() {
    }

    public static int paintFrom(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        if (stack.getItem() instanceof DyeItem) {
            DyeItem dye = (DyeItem) stack.getItem();
            return dye.getDyeColor().getId() + 1;
        }
        return -1;
    }
}
