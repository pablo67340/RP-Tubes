package com.rptubes.blockentity;

import com.rptubes.menu.AutoCraftingTableMk2Menu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.rptubes.inventory.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Port of Tubestuff's TileCraftingTable — the Automatic Crafting Table Mk II.
 *
 * Slot layout (identical to the original):
 *   0      output — the machine proactively crafts into it every tick
 *   1-9    ghost recipe grid — a template, items are never real
 *   10-54  input storage (45 slots); the last row (46-54) doubles as the
 *          overflow row for container items like empty buckets
 *
 * Each tick, if the output is empty, ingredients matching the ghost recipe
 * are located anywhere in the input storage, consumed, and the result placed
 * in the output. Automation pushes ingredients in and pulls the output out.
 */
public class AutoCraftingTableMk2BlockEntity extends BlockEntity implements Container, MenuProvider, com.rptubes.inventory.ItemHandlerProvider {
    public static final int SLOT_OUTPUT = 0;
    public static final int START_RECIPE = 1;
    public static final int SIZE_RECIPE = 9;
    public static final int START_INPUT = 10;
    public static final int SIZE_INPUT = 45;
    public static final int START_OVERFLOW = START_INPUT + 36;
    public static final int INV_SIZE = START_INPUT + SIZE_INPUT;

    private static final AbstractContainerMenu DUMMY_MENU = new AbstractContainerMenu(null, -1) {
        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }
    };

    private NonNullList<ItemStack> inv = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
    private final IItemHandler handler = new Mk2Handler();

    private CraftingRecipe cachedRecipe;
    private boolean recipeChanged = true;

    public AutoCraftingTableMk2BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACT_MK2, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AutoCraftingTableMk2BlockEntity be) {
        if (!be.inv.get(SLOT_OUTPUT).isEmpty()) {
            return;
        }
        // Re-resolve when the template changed; the null+ghost guard also
        // recovers from any missed change notification (e.g. merge-path
        // shift-clicks that bypass setItem)
        if (be.recipeChanged || (be.cachedRecipe == null && be.hasGhostTemplate())) {
            be.cacheRecipe();
            be.recipeChanged = false;
        }
        if (be.cachedRecipe != null) {
            be.makeOutput();
        }
    }

    private boolean hasGhostTemplate() {
        for (int k = 0; k < SIZE_RECIPE; k++) {
            if (!this.inv.get(k + START_RECIPE).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** What the ghost template resolves to — used by the GUI's faded result preview. */
    public ItemStack findRecipe() {
        if (this.level == null) {
            return ItemStack.EMPTY;
        }
        CraftingContainer matrix = this.makeMatrix(START_RECIPE);
        return this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, matrix, this.level)
                .map(r -> r.assemble(matrix, this.level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    private CraftingContainer makeMatrix(int start) {
        CraftingContainer matrix = new net.minecraft.world.inventory.TransientCraftingContainer(DUMMY_MENU, 3, 3);
        for (int k = 0; k < 9; k++) {
            matrix.setItem(k, this.inv.get(k + start).copy());
        }
        return matrix;
    }

    private void cacheRecipe() {
        CraftingContainer matrix = this.makeMatrix(START_RECIPE);
        this.cachedRecipe = this.level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, matrix, this.level)
                .orElse(null);
    }

    /** Port of OG makeOutput: map ghost slots to real input, consume, produce. */
    private void makeOutput() {
        // slotMap[ghost slot] = input slot supplying it; used[] guards double-booking
        int[] slotMap = new int[9];
        int[] used = new int[SIZE_INPUT];
        for (int k = 0; k < 9; k++) {
            ItemStack recipe = this.inv.get(k + START_RECIPE);
            slotMap[k] = -1;
            if (recipe.isEmpty()) {
                continue;
            }
            for (int i = 0; i < SIZE_INPUT; i++) {
                ItemStack input = this.inv.get(i + START_INPUT);
                if (input.isEmpty() || used[i] >= input.getCount()) {
                    continue;
                }
                if (ItemStack.isSameItemSameTags(recipe, input)) {
                    slotMap[k] = i;
                    used[i]++;
                    break;
                }
            }
            if (slotMap[k] == -1) {
                return;
            }
        }

        CraftingContainer matrix = new net.minecraft.world.inventory.TransientCraftingContainer(DUMMY_MENU, 3, 3);
        for (int k = 0; k < 9; k++) {
            if (slotMap[k] != -1) {
                ItemStack one = this.inv.get(slotMap[k] + START_INPUT).copy();
                one.setCount(1);
                matrix.setItem(k, one);
            }
        }
        if (!this.cachedRecipe.matches(matrix, this.level)) {
            return;
        }

        ItemStack result = this.cachedRecipe.assemble(matrix, this.level.registryAccess());
        if (result.isEmpty()) {
            return;
        }
        for (int k = 0; k < 9; k++) {
            if (slotMap[k] != -1) {
                this.inv.get(slotMap[k] + START_INPUT).shrink(1);
                if (this.inv.get(slotMap[k] + START_INPUT).isEmpty()) {
                    this.inv.set(slotMap[k] + START_INPUT, ItemStack.EMPTY);
                }
            }
        }
        NonNullList<ItemStack> remaining = this.cachedRecipe.getRemainingItems(matrix);
        for (ItemStack leftover : remaining) {
            if (!leftover.isEmpty()) {
                this.mergeIntoOverflow(leftover.copy());
            }
        }
        this.inv.set(SLOT_OUTPUT, result);
        this.setChanged();
    }

    /** OG mergeStackIntoRange targeting the overflow row; drops if there's no room. */
    private void mergeIntoOverflow(ItemStack stack) {
        for (int i = START_OVERFLOW; i < INV_SIZE && !stack.isEmpty(); i++) {
            ItemStack existing = this.inv.get(i);
            if (existing.isEmpty()) {
                this.inv.set(i, stack);
                return;
            }
            if (ItemStack.isSameItemSameTags(existing, stack)) {
                int room = existing.getMaxStackSize() - existing.getCount();
                int moved = Math.min(room, stack.getCount());
                existing.grow(moved);
                stack.shrink(moved);
            }
        }
        if (!stack.isEmpty()) {
            Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(),
                    this.worldPosition.getZ(), stack);
        }
    }

    /** Insert into input storage: merge with matching stacks first, then empty slots. */
    public ItemStack insertInput(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack remaining = stack.copy();
        for (int pass = 0; pass < 2 && !remaining.isEmpty(); pass++) {
            for (int i = START_INPUT; i < INV_SIZE && !remaining.isEmpty(); i++) {
                ItemStack existing = this.inv.get(i);
                if (pass == 0) {
                    if (existing.isEmpty() || !ItemStack.isSameItemSameTags(existing, remaining)) {
                        continue;
                    }
                    int room = existing.getMaxStackSize() - existing.getCount();
                    int moved = Math.min(room, remaining.getCount());
                    if (moved <= 0) {
                        continue;
                    }
                    if (!simulate) {
                        existing.grow(moved);
                    }
                    remaining.shrink(moved);
                } else if (existing.isEmpty()) {
                    if (!simulate) {
                        this.inv.set(i, remaining.copy());
                    }
                    remaining = ItemStack.EMPTY;
                }
            }
        }
        if (!simulate) {
            this.markInputChanged();
        }
        return remaining;
    }

    private void markInputChanged() {
        this.setChanged();
    }

    private void slotChanging(int slot) {
        if (slot >= START_RECIPE && slot < START_RECIPE + SIZE_RECIPE) {
            this.recipeChanged = true;
        }
    }

    /**
     * Automation view: slot 0 = output (extract only), slots 1-45 = input
     * storage (insert only, except the overflow row which is extractable so
     * container items like empty buckets can be piped back out).
     */
    public class Mk2Handler implements IItemHandler {
        @Override
        public int getSlots() {
            return 1 + SIZE_INPUT;
        }

        private int toInv(int slot) {
            return slot == 0 ? SLOT_OUTPUT : slot - 1 + START_INPUT;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return AutoCraftingTableMk2BlockEntity.this.inv.get(this.toInv(slot));
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot == 0) {
                return stack;
            }
            return AutoCraftingTableMk2BlockEntity.this.insertInput(stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int inv = this.toInv(slot);
            // Output always extractable; overflow row extractable for container items
            if (slot != 0 && inv < START_OVERFLOW) {
                return ItemStack.EMPTY;
            }
            ItemStack existing = AutoCraftingTableMk2BlockEntity.this.inv.get(inv);
            if (existing.isEmpty() || amount <= 0) {
                return ItemStack.EMPTY;
            }
            int taken = Math.min(amount, existing.getCount());
            ItemStack result = existing.copy();
            result.setCount(taken);
            if (!simulate) {
                existing.shrink(taken);
                if (existing.isEmpty()) {
                    AutoCraftingTableMk2BlockEntity.this.inv.set(inv, ItemStack.EMPTY);
                }
                AutoCraftingTableMk2BlockEntity.this.markInputChanged();
            }
            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != 0;
        }
    }

    // ---- Container (backs the GUI slots) ----

    @Override
    public int getContainerSize() {
        return INV_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inv) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inv.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.slotChanging(slot);
        return ContainerHelper.removeItem(this.inv, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.slotChanging(slot);
        return ContainerHelper.takeItem(this.inv, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.slotChanging(slot);
        this.inv.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.inv.clear();
    }

    // ---- MenuProvider ----

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.act_mk2");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AutoCraftingTableMk2Menu(id, inv, this);
    }

    // ---- persistence / caps ----

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inv);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inv = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inv);
        this.recipeChanged = true;
    }

    public IItemHandler getItemHandler(Direction side) {
        return this.handler;
    }

    @Override
    public void setRemoved() {
        if (this.level != null) {
            // Drop output + input storage, but never the ghost recipe templates
            for (int i = 0; i < INV_SIZE; i++) {
                if (i >= START_RECIPE && i < START_RECIPE + SIZE_RECIPE) {
                    continue;
                }
                ItemStack stack = this.inv.get(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(),
                            this.worldPosition.getZ(), stack);
                }
            }
        }
        super.setRemoved();
    }
}
