package com.rptubes.blockentity;

import com.rptubes.menu.AutoCraftingTableMk2Menu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

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
public class AutoCraftingTableMk2BlockEntity extends TileEntity implements net.minecraft.tileentity.ITickableTileEntity, IInventory, INamedContainerProvider {
    public static final int SLOT_OUTPUT = 0;
    public static final int START_RECIPE = 1;
    public static final int SIZE_RECIPE = 9;
    public static final int START_INPUT = 10;
    public static final int SIZE_INPUT = 45;
    public static final int START_OVERFLOW = START_INPUT + 36;
    public static final int INV_SIZE = START_INPUT + SIZE_INPUT;

    private static final Container DUMMY_MENU = new Container(null, -1) {
        @Override
        public ItemStack quickMoveStack(PlayerEntity player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(PlayerEntity player) {
            return false;
        }
    };

    private NonNullList<ItemStack> inv = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
    private LazyOptional<IItemHandler> handler = LazyOptional.of(Mk2Handler::new);

    private ICraftingRecipe cachedRecipe;
    private boolean recipeChanged = true;

    public AutoCraftingTableMk2BlockEntity() {
        super(ModBlockEntities.ACT_MK2.get());
    }

    public static void serverTick(World level, BlockPos pos, BlockState state, AutoCraftingTableMk2BlockEntity be) {
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
        CraftingInventory matrix = this.makeMatrix(START_RECIPE);
        return this.level.getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, matrix, this.level)
                .map(r -> r.assemble(matrix))
                .orElse(ItemStack.EMPTY);
    }

    private CraftingInventory makeMatrix(int start) {
        CraftingInventory matrix = new CraftingInventory(DUMMY_MENU, 3, 3);
        for (int k = 0; k < 9; k++) {
            matrix.setItem(k, this.inv.get(k + start).copy());
        }
        return matrix;
    }

    private void cacheRecipe() {
        CraftingInventory matrix = this.makeMatrix(START_RECIPE);
        this.cachedRecipe = this.level.getRecipeManager()
                .getRecipeFor(IRecipeType.CRAFTING, matrix, this.level)
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
                if ((ItemStack.isSame(recipe, input) && ItemStack.tagMatches(recipe, input))) {
                    slotMap[k] = i;
                    used[i]++;
                    break;
                }
            }
            if (slotMap[k] == -1) {
                return;
            }
        }

        CraftingInventory matrix = new CraftingInventory(DUMMY_MENU, 3, 3);
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

        ItemStack result = this.cachedRecipe.assemble(matrix);
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
            if ((ItemStack.isSame(existing, stack) && ItemStack.tagMatches(existing, stack))) {
                int room = existing.getMaxStackSize() - existing.getCount();
                int moved = Math.min(room, stack.getCount());
                existing.grow(moved);
                stack.shrink(moved);
            }
        }
        if (!stack.isEmpty()) {
            InventoryHelper.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(),
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
                    if (existing.isEmpty() || !(ItemStack.isSame(existing, remaining) && ItemStack.tagMatches(existing, remaining))) {
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
        public ItemStack getStackInSlot(int slot) {
            return AutoCraftingTableMk2BlockEntity.this.inv.get(this.toInv(slot));
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 0) {
                return stack;
            }
            return AutoCraftingTableMk2BlockEntity.this.insertInput(stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
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
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot != 0;
        }
    }

    // ---- IInventory (backs the GUI slots) ----

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
        return ItemStackHelper.removeItem(this.inv, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.slotChanging(slot);
        return ItemStackHelper.takeItem(this.inv, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.slotChanging(slot);
        this.inv.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.inv.clear();
    }

    // ---- INamedContainerProvider ----

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.rptubes.act_mk2");
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new AutoCraftingTableMk2Menu(id, inv, this);
    }

    // ---- persistence / caps ----

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        ItemStackHelper.saveAllItems(tag, this.inv);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.inv = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.inv);
        this.recipeChanged = true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.handler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handler = LazyOptional.of(Mk2Handler::new);
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
                    InventoryHelper.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(),
                            this.worldPosition.getZ(), stack);
                }
            }
        }
        super.setRemoved();
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        serverTick(this.level, this.worldPosition, this.getBlockState(), this);
    }
}
