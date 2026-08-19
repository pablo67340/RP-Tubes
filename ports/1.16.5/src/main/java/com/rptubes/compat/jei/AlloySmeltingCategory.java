package com.rptubes.compat.jei;

import com.rptubes.RPTubes;
import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlloySmeltingCategory implements IRecipeCategory<AlloyRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(RPTubes.MOD_ID, "alloy_smelting");

    private final IDrawable background;
    private final IDrawable icon;

    public AlloySmeltingCategory(IGuiHelper guiHelper) {
        ResourceLocation gui = new ResourceLocation(RPTubes.MOD_ID, "textures/gui/alloy_furnace.png");
        this.background = guiHelper.createDrawable(gui, 45, 16, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.ALLOY_FURNACE.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends AlloyRecipe> getRecipeClass() {
        return AlloyRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.get("jei.rptubes.alloy_smelting");
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
    public void setIngredients(AlloyRecipe recipe, IIngredients ingredients) {
        List<Ingredient> inputs = recipe.getIngredients();
        int[] counts = recipe.getCounts();
        List<List<ItemStack>> inputLists = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            int count = i < counts.length ? counts[i] : 1;
            inputLists.add(withCount(inputs.get(i), count));
        }
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputs(VanillaTypes.ITEM, Collections.singletonList(recipe.getResultItem()));
    }

    @Override
    public void setRecipe(IRecipeLayout layout, AlloyRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        List<Ingredient> inputs = recipe.getIngredients();
        for (int i = 0; i < inputs.size() && i < 9; i++) {
            int x = (i % 3) * 18 + 2;
            int y = (i / 3) * 18;
            stacks.init(i, true, x, y);
        }
        stacks.init(9, false, 95, 18);
        stacks.set(ingredients);
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
