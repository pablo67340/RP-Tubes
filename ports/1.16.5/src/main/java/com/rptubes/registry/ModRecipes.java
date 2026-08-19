package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.recipe.AlloyRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, RPTubes.MOD_ID);

    // 1.16 has no Forge registry for recipe types; register directly in the vanilla registry.
    public static final IRecipeType<AlloyRecipe> ALLOY_SMELTING_TYPE =
            IRecipeType.register(RPTubes.MOD_ID + ":alloy_smelting");

    public static final RegistryObject<IRecipeSerializer<AlloyRecipe>> ALLOY_SMELTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_smelting", AlloyRecipe.Serializer::new);

    private ModRecipes() {
    }
}
