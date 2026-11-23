package com.rptubes.registry;

import com.rptubes.RPTubes;
//? if forge {
/*import com.rptubes.recipe.AlloyRecipe;
*///?} else if fabric {
/*import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
*///?} else if <1.21.11 {
import net.minecraft.core.registries.Registries;
import com.rptubes.recipe.AlloyRecipe;
//?} else {
/*import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.core.registries.Registries;
*///?}
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
//? if forge {
/*import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?} else if neoforge && <1.21.11 {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} else if neoforge {
/*import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

public final class ModRecipes {
    //? if forge {
    /*public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, RPTubes.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, RPTubes.MOD_ID);

    public static final RegistryObject<RecipeType<AlloyRecipe>> ALLOY_SMELTING =
            RECIPE_TYPES.register("alloy_smelting", () -> new RecipeType<>() {
    *///?} else if neoforge {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, RPTubes.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, RPTubes.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyRecipe>> ALLOY_SMELTING =
            RECIPE_TYPES.register("alloy_smelting", () -> new RecipeType<>() {
    //?} else if <1.21.1 {
    /*public static final RecipeType<AlloyRecipe> ALLOY_SMELTING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            new ResourceLocation(RPTubes.MOD_ID, "alloy_smelting"),
            new RecipeType<>() {
    *///?} else {
    /*public static final RecipeType<AlloyRecipe> ALLOY_SMELTING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "alloy_smelting"),
            new RecipeType<>() {
    *///?}
                @Override
                public String toString() {
                    return RPTubes.MOD_ID + ":alloy_smelting";
                }
            });

    //? if fabric && <1.21.1 {
    /*public static final RecipeSerializer<AlloyRecipe> ALLOY_SMELTING_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            new ResourceLocation(RPTubes.MOD_ID, "alloy_smelting"),
            new AlloyRecipe.Serializer());

    public static void init() {
    }
    *///?} else if fabric && <26.1.2 {
    /*public static final RecipeSerializer<AlloyRecipe> ALLOY_SMELTING_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "alloy_smelting"),
            new AlloyRecipe.Serializer());

    public static void init() {
    }
    *///?} else if fabric {
    /*public static final RecipeSerializer<AlloyRecipe> ALLOY_SMELTING_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "alloy_smelting"),
            AlloyRecipe.createSerializer());

    public static void init() {
    }
    *///?} else if forge && >=26.1.2 {
    /*public static final RegistryObject<RecipeSerializer<AlloyRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", AlloyRecipe::createSerializer);
    *///?} else if >=26.1.2 {
    /*public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", AlloyRecipe::createSerializer);
    *///?} else if neoforge {
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", AlloyRecipe.Serializer::new);
    //?} else {
    /*public static final RegistryObject<RecipeSerializer<AlloyRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", AlloyRecipe.Serializer::new);
    *///?}

    private ModRecipes() {
    }
}
