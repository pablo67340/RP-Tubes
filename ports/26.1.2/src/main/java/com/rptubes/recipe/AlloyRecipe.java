package com.rptubes.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rptubes.registry.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Multi-input alloy recipe matching RedPower CraftLib alloy results.
 * Ingredients are consumed by count from the 3x3 input grid (order-independent).
 */
public class AlloyRecipe implements Recipe<net.minecraft.world.item.crafting.RecipeInput> {
    private final NonNullList<Ingredient> ingredients;
    private final int[] counts;
    private final ItemStackTemplate result;
    private final int cookTime;

    public AlloyRecipe(NonNullList<Ingredient> ingredients, int[] counts,
                       ItemStackTemplate result, int cookTime) {
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
    public boolean matches(net.minecraft.world.item.crafting.RecipeInput input, Level level) {
        int[] remaining = this.counts.clone();
        int slots = Math.min(9, input.size());
        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = input.getItem(slot);
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
    public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input) {
        return this.result.create();
    }

    // 26.1: group() and showNotification() became abstract on Recipe
    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
        return net.minecraft.world.item.crafting.PlacementInfo.NOT_PLACEABLE;
    }

    // Machine-only recipe; keeps it out of the vanilla recipe book plumbing.
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {
        return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
    }

    /** Direct result access for menu/JEI code that has no registry handle. */
    public ItemStack getResultStack() {
        return this.result.create();
    }

    @Override
    public RecipeSerializer<AlloyRecipe> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<AlloyRecipe> getType() {
        return ModRecipes.ALLOY_SMELTING.get();
    }

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

    /** JSON entry: { "ingredient": {...}, "count": n } */
    private record Entry(Ingredient ingredient, int count) {
        static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(Entry::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(Entry::count)
        ).apply(inst, Entry::new));

        static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, Entry::ingredient,
                net.minecraft.network.codec.ByteBufCodecs.VAR_INT, Entry::count,
                Entry::new);
    }

    private List<Entry> entries() {
        List<Entry> list = new ArrayList<>();
        for (int i = 0; i < this.ingredients.size(); i++) {
            list.add(new Entry(this.ingredients.get(i), this.counts[i]));
        }
        return list;
    }

    private static AlloyRecipe fromEntries(List<Entry> entries, ItemStackTemplate result, int cookTime) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        int[] counts = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            ingredients.add(entries.get(i).ingredient());
            counts[i] = entries.get(i).count();
        }
        return new AlloyRecipe(ingredients, counts, result, cookTime);
    }

    // 26.1: RecipeSerializer is now a record, so we just build one from codecs
    private static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.list(Entry.CODEC).fieldOf("ingredients").forGetter(AlloyRecipe::entries),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
            Codec.INT.optionalFieldOf("cookTime", 200).forGetter(AlloyRecipe::getCookTime)
    ).apply(inst, AlloyRecipe::fromEntries));

    private static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), AlloyRecipe::entries,
            ItemStackTemplate.STREAM_CODEC, r -> r.result,
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT, AlloyRecipe::getCookTime,
            AlloyRecipe::fromEntries);

    public static RecipeSerializer<AlloyRecipe> createSerializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
