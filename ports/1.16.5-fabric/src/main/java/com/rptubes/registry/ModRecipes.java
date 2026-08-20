package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

public final class ModRecipes {
    public static final RecipeType<AlloyRecipe> ALLOY_SMELTING_TYPE =
            RecipeType.register(RPTubes.MOD_ID + ":alloy_smelting");

    public static final RecipeSerializer<AlloyRecipe> ALLOY_SMELTING_SERIALIZER = Registry.register(
            Registry.RECIPE_SERIALIZER,
            new ResourceLocation(RPTubes.MOD_ID, "alloy_smelting"),
            new AlloyRecipe.Serializer());

    public static void init() {
    }

    private ModRecipes() {
    }
}
