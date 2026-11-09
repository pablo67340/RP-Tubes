package com.rptubes.tube;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** Resolve a held stack to a tube paint value (1-16), or -1 if it is not a dye. */
public final class DyeHelper {
    private DyeHelper() {
    }

    public static int paintFrom(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        if (stack.getItem() instanceof ItemDye) {
            return EnumDyeColor.byDyeDamage(stack.getMetadata()).getMetadata() + 1;
        }
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            if (!name.startsWith("dye") || name.length() <= 3 || name.equals("dye")) {
                continue;
            }
            String rest = name.substring(3);
            for (EnumDyeColor dye : EnumDyeColor.values()) {
                if (rest.equalsIgnoreCase(dye.getDyeColorName())
                        || rest.equalsIgnoreCase(dye.getName())) {
                    return dye.getMetadata() + 1;
                }
            }
        }
        return -1;
    }
}
