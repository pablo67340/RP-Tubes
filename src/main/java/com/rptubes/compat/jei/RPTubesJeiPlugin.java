//? if jei {
package com.rptubes.compat.jei;

import com.rptubes.RPTubes;
import com.rptubes.client.AlloyFurnaceScreen;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import com.rptubes.registry.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

/**
 * JEI: custom Alloy Smelting category + transfer/click areas.
 * Shaped/shapeless crafting recipes for all machines are picked up automatically by JEI.
 */
@JeiPlugin
public class RPTubesJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        //? if <1.21.1 {
        /*return new ResourceLocation(RPTubes.MOD_ID, "jei");
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "jei");
        //?}
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        RecipeManager recipes = mc.level.getRecipeManager();
        //? if forge && <1.20.6 {
        /*List<AlloyRecipe> alloy = recipes.getAllRecipesFor(ModRecipes.ALLOY_SMELTING.get());
        *///?} else if <1.20.6 {
        /*List<AlloyRecipe> alloy = recipes.getAllRecipesFor(ModRecipes.ALLOY_SMELTING);
        *///?} else if fabric {
        /*List<AlloyRecipe> alloy = recipes.getAllRecipesFor(ModRecipes.ALLOY_SMELTING).stream()
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .toList();
        *///?} else {
        List<AlloyRecipe> alloy = recipes.getAllRecipesFor(ModRecipes.ALLOY_SMELTING.get()).stream()
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .toList();
        //?}
        registration.addRecipes(AlloySmeltingCategory.RECIPE_TYPE, alloy);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        //? if forgelike {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALLOY_FURNACE.get()),
        //?} else {
        /*registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALLOY_FURNACE),
        *///?}
                AlloySmeltingCategory.RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlloyFurnaceScreen.class, 96, 34, 24, 16,
                AlloySmeltingCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // slots 0-8 = alloy inputs; player inv starts at 11 (after fuel+output)
        //? if forgelike {
        registration.addRecipeTransferHandler(AlloyFurnaceMenu.class, ModMenus.ALLOY_FURNACE.get(),
        //?} else {
        /*registration.addRecipeTransferHandler(AlloyFurnaceMenu.class, ModMenus.ALLOY_FURNACE,
        *///?}
                AlloySmeltingCategory.RECIPE_TYPE, 0, 9, 11, 36);
    }
}
//?}
