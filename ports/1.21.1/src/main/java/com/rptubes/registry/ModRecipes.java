package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, RPTubes.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, RPTubes.MOD_ID);

    public static final RegistryObject<RecipeType<AlloyRecipe>> ALLOY_SMELTING =
            RECIPE_TYPES.register("alloy_smelting", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return RPTubes.MOD_ID + ":alloy_smelting";
                }
            });

    public static final RegistryObject<RecipeSerializer<AlloyRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", AlloyRecipe.Serializer::new);

    private ModRecipes() {
    }
}
