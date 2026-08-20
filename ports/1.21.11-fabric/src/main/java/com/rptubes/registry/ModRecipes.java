package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {
    public static final RecipeType<AlloyRecipe> ALLOY_SMELTING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, "alloy_smelting"),
            new RecipeType<>() {
                @Override
                public String toString() {
                    return RPTubes.MOD_ID + ":alloy_smelting";
                }
            });

    public static final RecipeSerializer<AlloyRecipe> ALLOY_SMELTING_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, "alloy_smelting"),
            new AlloyRecipe.Serializer());

    public static void init() {
    }

    private ModRecipes() {
    }
}
