package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, RPTubes.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, RPTubes.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyRecipe>> ALLOY_SMELTING =
            RECIPE_TYPES.register("alloy_smelting", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return RPTubes.MOD_ID + ":alloy_smelting";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", AlloyRecipe.Serializer::new);

    private ModRecipes() {
    }
}
