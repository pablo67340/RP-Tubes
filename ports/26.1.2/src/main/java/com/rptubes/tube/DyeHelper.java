package com.rptubes.tube;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

/** Resolve a held stack to a tube paint value (1-16), or -1 if it is not a dye. */
public final class DyeHelper {
    private DyeHelper() {
    }

    public static int paintFrom(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        if (stack.getItem() instanceof DyeItem) {
            DyeColor color = DyeColor.getColor(stack);
            if (color != null) {
                return color.getId() + 1;
            }
        }
        DyeColor fromStack = DyeColor.getColor(stack);
        return fromStack == null ? -1 : fromStack.getId() + 1;
    }
}
