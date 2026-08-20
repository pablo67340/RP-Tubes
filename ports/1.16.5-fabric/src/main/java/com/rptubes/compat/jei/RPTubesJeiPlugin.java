package com.rptubes.compat.jei;

import com.rptubes.RPTubes;
import com.rptubes.client.AlloyFurnaceScreen;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.registry.ModBlocks;
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
        return new ResourceLocation(RPTubes.MOD_ID, "jei");
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
        List<AlloyRecipe> alloy = recipes.getAllRecipesFor(ModRecipes.ALLOY_SMELTING_TYPE);
        registration.addRecipes(alloy, AlloySmeltingCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALLOY_FURNACE),
                AlloySmeltingCategory.UID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlloyFurnaceScreen.class, 96, 34, 24, 16,
                AlloySmeltingCategory.UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // slots 0-8 = alloy inputs; player inv starts at 11 (after fuel+output)
        registration.addRecipeTransferHandler(AlloyFurnaceMenu.class,
                AlloySmeltingCategory.UID, 0, 9, 11, 36);
    }
}
