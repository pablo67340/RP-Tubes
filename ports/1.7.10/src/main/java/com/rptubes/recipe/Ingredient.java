package com.rptubes.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Tiny stand-in for 1.12's Ingredient/OreIngredient: an item predicate
 * built from explicit stacks or an ore dictionary name.
 */
public abstract class Ingredient {

    public abstract boolean apply(ItemStack stack);

    public static Ingredient fromStacks(final ItemStack... stacks) {
        return new Ingredient() {
            @Override
            public boolean apply(ItemStack stack) {
                if (com.rptubes.util.Stacks.isEmpty(stack)) {
                    return false;
                }
                for (ItemStack option : stacks) {
                    if (stack.getItem() == option.getItem()
                            && (option.getItemDamage() == OreDictionary.WILDCARD_VALUE
                            || stack.getItemDamage() == option.getItemDamage())) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Ingredient fromOre(final String oreName) {
        return new Ingredient() {
            @Override
            public boolean apply(ItemStack stack) {
                if (com.rptubes.util.Stacks.isEmpty(stack)) {
                    return false;
                }
                List<ItemStack> ores = OreDictionary.getOres(oreName);
                for (ItemStack option : ores) {
                    if (OreDictionary.itemMatches(option, stack, false)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
