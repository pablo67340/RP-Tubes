package com.rptubes.recipe;

//? if <1.20.6 {
/*import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
*///?} else {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import com.rptubes.registry.ModRecipes;
import net.minecraft.core.NonNullList;
//? if <1.20.6 {
/*import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
*///?} else {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?}
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
//? if >=26.1.2 {
/*import net.minecraft.world.item.ItemStackTemplate;
*///?}
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
//? if >=1.21.1 && <1.21.11 {
import net.minecraft.world.item.crafting.RecipeInput;
//?}
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
//? if forge && <1.20.6 {
/*import net.minecraftforge.common.crafting.CraftingHelper;
*///?} else if <1.20.6 {
/*import net.minecraft.world.item.crafting.ShapedRecipe;
*///?}

import java.util.ArrayList;
import java.util.List;

/**
 * Multi-input alloy recipe matching RedPower CraftLib alloy results.
 * Ingredients are consumed by count from the 3x3 input grid (order-independent).
 */
//? if <1.20.6 {
/*public class AlloyRecipe implements Recipe<Container> {
    private final ResourceLocation id;
*///?} else if <1.21.1 {
/*public class AlloyRecipe implements Recipe<Container> {
*///?} else if <1.21.11 {
public class AlloyRecipe implements Recipe<RecipeInput> {
//?} else {
/*public class AlloyRecipe implements Recipe<net.minecraft.world.item.crafting.RecipeInput> {
*///?}
    private final NonNullList<Ingredient> ingredients;
    private final int[] counts;
    //? if <26.1.2 {
    private final ItemStack result;
    //?} else {
    /*private final ItemStackTemplate result;
    *///?}
    private final int cookTime;

    //? if <1.20.6 {
    /*public AlloyRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, int[] counts,
                       ItemStack result, int cookTime) {
        this.id = id;
    *///?} else if <26.1.2 {
    public AlloyRecipe(NonNullList<Ingredient> ingredients, int[] counts,
                       ItemStack result, int cookTime) {
    //?} else {
    /*public AlloyRecipe(NonNullList<Ingredient> ingredients, int[] counts,
                       ItemStackTemplate result, int cookTime) {
    *///?}
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
    //? if <1.20.6 {
    /*public boolean matches(Container container, Level level) {
        int[] remaining = this.counts.clone();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = container.getItem(slot);
    *///?} else if <1.21.1 {
    /*public boolean matches(Container input, Level level) {
        int[] remaining = this.counts.clone();
        int slots = Math.min(9, input.getContainerSize());
        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = input.getItem(slot);
    *///?} else if <1.21.11 {
    public boolean matches(RecipeInput input, Level level) {
        int[] remaining = this.counts.clone();
        int slots = Math.min(9, input.size());
        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = input.getItem(slot);
    //?} else {
    /*public boolean matches(net.minecraft.world.item.crafting.RecipeInput input, Level level) {
        int[] remaining = this.counts.clone();
        int slots = Math.min(9, input.size());
        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = input.getItem(slot);
    *///?}
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
    //? if <1.20.1 {
    /*public ItemStack assemble(Container container) {
        return this.result.copy();
    *///?} else if <1.20.6 {
    /*public ItemStack assemble(Container container, net.minecraft.core.RegistryAccess registryAccess) {
        return this.result.copy();
    *///?} else if <1.21.1 {
    /*public ItemStack assemble(Container input, net.minecraft.core.HolderLookup.Provider registries) {
        return this.result.copy();
    *///?} else if <1.21.11 {
    public ItemStack assemble(RecipeInput input, net.minecraft.core.HolderLookup.Provider registries) {
        return this.result.copy();
    //?} else if <26.1.2 {
    /*public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider registries) {
        return this.result.copy();
    *///?} else {
    /*public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input) {
        return this.result.create();
    }

    // 26.1: group() and showNotification() became abstract on Recipe
    @Override
    public String group() {
        return "";
    *///?}
    }

    @Override
    //? if <1.21.11 {
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    //?} else if <26.1.2 {
    /*public net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
        return net.minecraft.world.item.crafting.PlacementInfo.NOT_PLACEABLE;
    }

    // Machine-only recipe; keeps it out of the vanilla recipe book plumbing.
    @Override
    public boolean isSpecial() {
        return true;
    *///?} else {
    /*public boolean showNotification() {
        return false;
    *///?}
    }

    @Override
    //? if <1.20.1 {
    /*public ItemStack getResultItem() {
        return this.result;
    *///?} else if <1.20.6 {
    /*public ItemStack getResultItem(net.minecraft.core.RegistryAccess registryAccess) {
        return this.result;
    *///?} else if <1.21.11 {
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registries) {
        return this.result;
    //?} else if <26.1.2 {
    /*public net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {
        return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
    *///?} else {
    /*public net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
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
    *///?}
    }

    /** Direct result access for menu/JEI code that has no registry handle. */
    public ItemStack getResultStack() {
        //? if forge && <1.20.6 {
        /*return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
        *///?} else if <26.1.2 {
        return this.result;
        //?} else {
        /*return this.result.create();
        *///?}
    }

    @Override
    //? if forgelike && <1.21.11 {
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER.get();
    //?} else if <1.20.6 {
    /*public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER;
    *///?} else if <1.21.11 {
    /*public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER;
    *///?} else if fabric {
    /*public RecipeSerializer<AlloyRecipe> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER;
    *///?} else {
    /*public RecipeSerializer<AlloyRecipe> getSerializer() {
        return ModRecipes.ALLOY_SMELTING_SERIALIZER.get();
    *///?}
    }

    @Override
    //? if forgelike && <1.21.11 {
    public RecipeType<?> getType() {
        return ModRecipes.ALLOY_SMELTING.get();
    //?} else if <1.21.11 {
    /*public RecipeType<?> getType() {
        return ModRecipes.ALLOY_SMELTING;
    *///?} else if fabric {
    /*public RecipeType<AlloyRecipe> getType() {
        return ModRecipes.ALLOY_SMELTING;
    *///?} else {
    /*public RecipeType<AlloyRecipe> getType() {
        return ModRecipes.ALLOY_SMELTING.get();
    *///?}
    }

    //? if <1.21.11 {
    @Override
    //?}
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

    //? if >=26.1.2 {
    /*/^* JSON entry: { "ingredient": {...}, "count": n } ^/
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
    *///?} else if >=1.21.11 {
    /*/^* JSON entry: { "ingredient": {...}, "count": n } ^/
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

    private static AlloyRecipe fromEntries(List<Entry> entries, ItemStack result, int cookTime) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        int[] counts = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            ingredients.add(entries.get(i).ingredient());
            counts[i] = entries.get(i).count();
        }
        return new AlloyRecipe(ingredients, counts, result, cookTime);
    }

    public static class Serializer implements RecipeSerializer<AlloyRecipe> {
        private static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.list(Entry.CODEC).fieldOf("ingredients").forGetter(AlloyRecipe::entries),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.optionalFieldOf("cookTime", 200).forGetter(AlloyRecipe::getCookTime)
        ).apply(inst, AlloyRecipe::fromEntries));

        private static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC = StreamCodec.composite(
                Entry.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), AlloyRecipe::entries,
                ItemStack.STREAM_CODEC, r -> r.result,
                net.minecraft.network.codec.ByteBufCodecs.VAR_INT, AlloyRecipe::getCookTime,
                AlloyRecipe::fromEntries);

        @Override
        public MapCodec<AlloyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    *///?} else if >=1.20.6 {
    /** JSON entry: { "ingredient": {...}, "count": n } */
    private record Entry(Ingredient ingredient, int count) {
        static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(Entry::ingredient),
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

    private static AlloyRecipe fromEntries(List<Entry> entries, ItemStack result, int cookTime) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        int[] counts = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            ingredients.add(entries.get(i).ingredient());
            counts[i] = entries.get(i).count();
        }
        return new AlloyRecipe(ingredients, counts, result, cookTime);
    }

    public static class Serializer implements RecipeSerializer<AlloyRecipe> {
        private static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.list(Entry.CODEC).fieldOf("ingredients").forGetter(AlloyRecipe::entries),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.optionalFieldOf("cookTime", 200).forGetter(AlloyRecipe::getCookTime)
        ).apply(inst, AlloyRecipe::fromEntries));

        private static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC = StreamCodec.composite(
                Entry.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), AlloyRecipe::entries,
                ItemStack.STREAM_CODEC, r -> r.result,
                net.minecraft.network.codec.ByteBufCodecs.VAR_INT, AlloyRecipe::getCookTime,
                AlloyRecipe::fromEntries);

        @Override
        public MapCodec<AlloyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    //?} else if forge {
    /*public static class Serializer implements RecipeSerializer<AlloyRecipe> {
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
    *///?} else {
    /*public static class Serializer implements RecipeSerializer<AlloyRecipe> {
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
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
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
    *///?}
    }
}
