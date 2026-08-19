package com.rptubes.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rptubes.registry.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Multi-input alloy recipe matching RedPower CraftLib alloy results.
 * Ingredients are consumed by count from the 3x3 input grid (order-independent).
 */
public class AlloyRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final int[] counts;
    private final ItemStack result;
    private final int cookTime;

    public AlloyRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, int[] counts,
                       ItemStack result, int cookTime) {
        this.id = id;
        this.ingredients = ingredients;
        this.counts = counts;
        this.result = result;
        this.cookTime = cookTime;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    public int[] getCounts() {
        return this.counts.clone();
    }

    @Override
    public boolean matches(Container container, Level level) {
        int[] remaining = this.counts.clone();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            int left = stack.getCount();
            for (int i = 0; i < this.ingredients.size() && left > 0; i++) {
                if (remaining[i] <= 0) {
                    continue;
                }
                if (!this.ingredients.get(i).test(stack)) {
                    continue;
                }
                int used = Math.min(remaining[i], left);
                remaining[i] -= used;
                left -= used;
            }
            if (left > 0) {
                return false;
            }
        }
        for (int need : remaining) {
            if (need > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, net.minecraft.core.RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registryAccess) {
        return this.result;
    }

    /** Direct result access for menu/JEI code that has no registry handle. */
    public ItemStack getResultStack() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALLOY_SMELTING.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    /** Consume matched inputs from slots 0-8. */
    public void consumeInputs(Container container) {
        int[] remaining = this.counts.clone();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            for (int i = 0; i < this.ingredients.size(); i++) {
                if (remaining[i] <= 0) {
                    continue;
                }
                if (!this.ingredients.get(i).test(stack)) {
                    continue;
                }
                int used = Math.min(remaining[i], stack.getCount());
                stack.shrink(used);
                remaining[i] -= used;
                if (stack.isEmpty()) {
                    container.setItem(slot, ItemStack.EMPTY);
                }
                if (remaining[i] == 0) {
                    break;
                }
            }
        }
    }

    public static class Serializer implements RecipeSerializer<AlloyRecipe> {
        @Override
        public AlloyRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            List<Integer> counts = new ArrayList<>();
            for (JsonElement element : ingredientsJson) {
                JsonObject obj = element.getAsJsonObject();
                ingredients.add(Ingredient.fromJson(obj));
                counts.add(GsonHelper.getAsInt(obj, "count", 1));
            }
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            int cookTime = GsonHelper.getAsInt(json, "cookTime", 200);
            int[] countArr = counts.stream().mapToInt(Integer::intValue).toArray();
            return new AlloyRecipe(id, ingredients, countArr, result, cookTime);
        }

        @Override
        public AlloyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            int[] counts = new int[size];
            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
                counts[i] = buf.readVarInt();
            }
            ItemStack result = buf.readItem();
            int cookTime = buf.readVarInt();
            return new AlloyRecipe(id, ingredients, counts, result, cookTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, AlloyRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (int i = 0; i < recipe.ingredients.size(); i++) {
                recipe.ingredients.get(i).toNetwork(buf);
                buf.writeVarInt(recipe.counts[i]);
            }
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.cookTime);
        }
    }
}
