package com.rptubes.registry;

import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.recipe.Ingredient;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.7.10 has no JSON recipes at all, so alloy smelting recipes are
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
            List<Ingredient> ingredients = new ArrayList<Ingredient>();
            ingredients.add(Ingredient.fromOre("ingotCopper"));
            ingredients.add(Ingredient.fromOre("ingotTin"));
            ALLOY_RECIPES.add(new AlloyRecipe(ingredients, new int[]{3, 1},
                    new ItemStack(ModItems.BRASS_INGOT.get(), 4), 200));
        }

        // Red alloy ingot: 4 redstone + 1 iron -> 1 red alloy (when ProjectRed provides it)
        List<ItemStack> redAlloy = OreDictionary.getOres("ingotRedAlloy");
        if (!redAlloy.isEmpty()) {
            ItemStack result = redAlloy.get(0).copy();
            result.stackSize = 1;
            List<Ingredient> ingredients = new ArrayList<Ingredient>();
            ingredients.add(Ingredient.fromStacks(new ItemStack(net.minecraft.init.Items.redstone)));
            ingredients.add(Ingredient.fromOre("ingotIron"));
            ALLOY_RECIPES.add(new AlloyRecipe(ingredients, new int[]{4, 1}, result, 200));
        }

        // Tin ore smelting (replaces the smelting/blasting JSONs)
        GameRegistry.addSmelting(ModBlocks.TIN_ORE.get(),
                new ItemStack(ModItems.TIN_INGOT.get()), 0.7F);
    }

    private ModRecipes() {
    }
}
