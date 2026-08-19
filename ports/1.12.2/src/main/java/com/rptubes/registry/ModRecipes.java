package com.rptubes.registry;

import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.12 has no custom JSON recipe types, so alloy smelting recipes are
 * registered in code with ore dictionary ingredients.
 */
public final class ModRecipes {
    private static final List<AlloyRecipe> ALLOY_RECIPES = new ArrayList<AlloyRecipe>();

    public static List<AlloyRecipe> getAlloyRecipes() {
        return ALLOY_RECIPES;
    }

    public static AlloyRecipe findAlloyRecipe(IInventory inv) {
        for (AlloyRecipe recipe : ALLOY_RECIPES) {
            if (recipe.matches(inv)) {
                return recipe;
            }
        }
        return null;
    }

    /** Call during init, after ore dictionary entries from all mods are in place. */
    public static void register() {
        // Brass: 3 copper + 1 tin -> 4 brass (OG CraftLib alloy)
        if (!OreDictionary.getOres("ingotCopper").isEmpty()) {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(new OreIngredient("ingotCopper"));
            ingredients.add(new OreIngredient("ingotTin"));
            ALLOY_RECIPES.add(new AlloyRecipe(ingredients, new int[]{3, 1},
                    new ItemStack(ModItems.BRASS_INGOT.get(), 4), 200));
        }

        // Red alloy ingot: 4 redstone + 1 iron -> 1 red alloy (when ProjectRed provides it)
        List<ItemStack> redAlloy = OreDictionary.getOres("ingotRedAlloy");
        if (!redAlloy.isEmpty()) {
            ItemStack result = redAlloy.get(0).copy();
            result.setCount(1);
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(Ingredient.fromStacks(new ItemStack(net.minecraft.init.Items.REDSTONE)));
            ingredients.add(new OreIngredient("ingotIron"));
            ALLOY_RECIPES.add(new AlloyRecipe(ingredients, new int[]{4, 1}, result, 200));
        }

        // Tin ore smelting (replaces the smelting/blasting JSONs)
        GameRegistry.addSmelting(ModBlocks.TIN_ORE.get(),
                new ItemStack(ModItems.TIN_INGOT.get()), 0.7F);
    }

    private ModRecipes() {
    }
}
