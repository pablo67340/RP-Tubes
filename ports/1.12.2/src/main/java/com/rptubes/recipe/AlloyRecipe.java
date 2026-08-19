package com.rptubes.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

/**
 * Multi-input alloy recipe matching RedPower CraftLib alloy results.
 * Ingredients are consumed by count from the 3x3 input grid (order-independent).
 * On 1.12 these are registered in code (see ModRecipes) instead of JSON.
 */
public class AlloyRecipe {
    private final NonNullList<Ingredient> ingredients;
    private final int[] counts;
    private final ItemStack result;
    private final int cookTime;

    public AlloyRecipe(NonNullList<Ingredient> ingredients, int[] counts, ItemStack result, int cookTime) {
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

    public ItemStack getResultItem() {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ItemStack assemble() {
        return this.result.copy();
    }

    public boolean matches(IInventory container) {
        int[] remaining = this.counts.clone();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = container.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            int left = stack.getCount();
            for (int i = 0; i < this.ingredients.size() && left > 0; i++) {
                if (remaining[i] <= 0) {
                    continue;
                }
                if (!this.ingredients.get(i).apply(stack)) {
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

    /** Consume matched inputs from slots 0-8. */
    public void consumeInputs(IInventory container) {
        int[] remaining = this.counts.clone();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = container.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            for (int i = 0; i < this.ingredients.size(); i++) {
                if (remaining[i] <= 0) {
                    continue;
                }
                if (!this.ingredients.get(i).apply(stack)) {
                    continue;
                }
                int used = Math.min(remaining[i], stack.getCount());
                stack.shrink(used);
                remaining[i] -= used;
                if (stack.isEmpty()) {
                    container.setInventorySlotContents(slot, ItemStack.EMPTY);
                }
                if (remaining[i] == 0) {
                    break;
                }
            }
        }
    }
}
