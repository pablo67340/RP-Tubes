package com.rptubes.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rptubes.registry.ModRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.JSONUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Multi-input alloy recipe matching RedPower CraftLib alloy results.
 * Ingredients are consumed by count from the 3x3 input grid (order-independent).
 */
public class AlloyRecipe implements IRecipe<IInventory> {
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
    public boolean matches(IInventory container, World level) {
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
    public ItemStack assemble(IInventory container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.ALLOY_SMELTING_TYPE;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    /** Consume matched inputs from slots 0-8. */
    public void consumeInputs(IInventory container) {
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

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<AlloyRecipe> {
        @Override
        public AlloyRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = JSONUtils.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            List<Integer> counts = new ArrayList<>();
            for (JsonElement element : ingredientsJson) {
                JsonObject obj = element.getAsJsonObject();
                ingredients.add(Ingredient.fromJson(obj));
                counts.add(JSONUtils.getAsInt(obj, "count", 1));
            }
            ItemStack result = CraftingHelper.getItemStack(JSONUtils.getAsJsonObject(json, "result"), true);
            int cookTime = JSONUtils.getAsInt(json, "cookTime", 200);
            int[] countArr = counts.stream().mapToInt(Integer::intValue).toArray();
            return new AlloyRecipe(id, ingredients, countArr, result, cookTime);
        }

        @Override
        public AlloyRecipe fromNetwork(ResourceLocation id, PacketBuffer buf) {
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
        public void toNetwork(PacketBuffer buf, AlloyRecipe recipe) {
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
