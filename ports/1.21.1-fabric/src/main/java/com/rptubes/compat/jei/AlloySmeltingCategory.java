package com.rptubes.compat.jei;

import com.rptubes.RPTubes;
import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AlloySmeltingCategory implements IRecipeCategory<AlloyRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "alloy_smelting");
    public static final RecipeType<AlloyRecipe> RECIPE_TYPE =
            RecipeType.create(RPTubes.MOD_ID, "alloy_smelting", AlloyRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public AlloySmeltingCategory(IGuiHelper guiHelper) {
        ResourceLocation gui = ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/alloy_furnace.png");
        this.background = guiHelper.createDrawable(gui, 45, 16, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.ALLOY_FURNACE));
    }

    @Override
    public RecipeType<AlloyRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.rptubes.alloy_smelting");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.getIngredients();
        int[] counts = recipe.getCounts();
        for (int i = 0; i < ingredients.size() && i < 9; i++) {
            int x = (i % 3) * 18 + 3;
            int y = (i / 3) * 18 + 1;
            int count = i < counts.length ? counts[i] : 1;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStacks(withCount(ingredients.get(i), count));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 19)
                .addItemStack(recipe.getResultStack());
    }

    private static List<ItemStack> withCount(Ingredient ingredient, int count) {
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            ItemStack copy = stack.copy();
            copy.setCount(Math.max(1, count));
            stacks.add(copy);
        }
        return stacks;
    }
}
