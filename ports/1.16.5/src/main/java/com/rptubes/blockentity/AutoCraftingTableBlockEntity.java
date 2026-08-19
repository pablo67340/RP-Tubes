package com.rptubes.blockentity;

import com.rptubes.menu.AutoCraftingTableMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Port of BuildCraft 2.2.14 TileAutoWorkbench — the Automatic Crafting Table.
 *
 * The 3x3 grid holds REAL items. Automation (transposer/filter/retriever)
 * crafts one result per pull, always leaving the last item of each ingredient
 * in place so the recipe never breaks; when a slot is down to its final item,
 * the missing ingredient is pulled from an adjacent inventory instead (the
 * classic "chest next to the table" setup). GUI crafting behaves like a
 * normal crafting table and may consume the grid entirely. IInventory items
 * (buckets etc.) are returned where the ingredient came from.
 */
public class AutoCraftingTableBlockEntity extends TileEntity implements IInventory, INamedContainerProvider {
    /** CraftingInventory demands a menu; recipe lookups never touch it. */
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

    private NonNullList<ItemStack> grid = NonNullList.withSize(9, ItemStack.EMPTY);
    private LazyOptional<IItemHandler> handler = LazyOptional.of(AutoCraftHandler::new);

    public AutoCraftingTableBlockEntity() {
        super(ModBlockEntities.AUTO_CRAFTING_TABLE.get());
    }

    /** What the current grid would craft, without consuming anything. */
    public ItemStack findRecipe() {
        if (this.level == null) {
            return ItemStack.EMPTY;
        }
        CraftingInventory matrix = new CraftingInventory(DUMMY_MENU, 3, 3);
        for (int i = 0; i < 9; i++) {
            matrix.setItem(i, this.grid.get(i).copy());
        }
        return this.level.getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, matrix, this.level)
                .map(r -> r.assemble(matrix))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Port of OG extractItem(doRemove, removeRecipe): perform one craft.
     * removeRecipe=false (automation) leaves the last item of each ingredient
     * in the grid, pulling replacements from adjacent inventories.
     * removeRecipe=true (GUI take) consumes grid items directly.
     * doRemove=false simulates: ingredients are taken then rolled back.
     */
    public ItemStack craftOne(boolean doRemove, boolean removeRecipe) {
        if (this.level == null || this.level.isClientSide) {
            return ItemStack.EMPTY;
        }
        CraftingInventory matrix = new CraftingInventory(DUMMY_MENU, 3, 3);
        List<StackPointer> pointers = new ArrayList<>();
        StackPointer[] byMatrixSlot = new StackPointer[9];
        int itemsToLeave = removeRecipe ? 0 : 1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.grid.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            StackPointer pointer;
            if (stack.getCount() <= itemsToLeave) {
                pointer = this.takeNearbyItem(stack);
                if (pointer == null) {
                    this.rollback(pointers);
                    return ItemStack.EMPTY;
                }
            } else {
                ItemStack taken = stack.split(1);
                pointer = new StackPointer(null, i, taken);
            }
            pointers.add(pointer);
            byMatrixSlot[i] = pointer;
            matrix.setItem(i, pointer.taken.copy());
        }

        Optional<ICraftingRecipe> recipe =
                this.level.getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, matrix, this.level);
        if (!recipe.isPresent()) {
            this.rollback(pointers);
            return ItemStack.EMPTY;
        }
        ItemStack result = recipe.get().assemble(matrix);
        if (result.isEmpty() || !doRemove) {
            this.rollback(pointers);
            return result;
        }

        NonNullList<ItemStack> remaining = recipe.get().getRemainingItems(matrix);
        for (int i = 0; i < 9; i++) {
            ItemStack remainder = remaining.get(i);
            if (!remainder.isEmpty() && byMatrixSlot[i] != null) {
                this.returnRemainder(byMatrixSlot[i], remainder);
            }
        }
        this.setChanged();
        return result;
    }

    /**
     * OG getNearbyItem: search all six neighbors (plain inventories only —
     * never tubes or other crafting tables) for one matching ingredient.
     */
    private StackPointer takeNearbyItem(ItemStack want) {
        for (Direction dir : Direction.values()) {
            BlockPos npos = this.worldPosition.relative(dir);
            TileEntity nbe = this.level.getBlockEntity(npos);
            if (nbe == null || nbe instanceof ITubeConnectable || nbe instanceof AutoCraftingTableBlockEntity) {
                continue;
            }
            IItemHandler neighbor =
                    nbe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).orElse(null);
            if (neighbor == null) {
                continue;
            }
            for (int s = 0; s < neighbor.getSlots(); s++) {
                if (!(ItemStack.isSame(neighbor.getStackInSlot(s), want) && ItemStack.tagMatches(neighbor.getStackInSlot(s), want))) {
                    continue;
                }
                ItemStack taken = neighbor.extractItem(s, 1, false);
                if (taken.isEmpty()) {
                    continue;
                }
                if (!(ItemStack.isSame(taken, want) && ItemStack.tagMatches(taken, want))) {
                    ItemHandlerHelper.insertItem(neighbor, taken, false);
                    continue;
                }
                return new StackPointer(neighbor, s, taken);
            }
        }
        return null;
    }

    private void rollback(List<StackPointer> pointers) {
        for (StackPointer p : pointers) {
            if (p.handler == null) {
                ItemStack current = this.grid.get(p.index);
                if (current.isEmpty()) {
                    this.grid.set(p.index, p.taken);
                } else {
                    current.grow(p.taken.getCount());
                }
            } else {
                ItemStack leftover = p.handler.insertItem(p.index, p.taken, false);
                if (!leftover.isEmpty()) {
                    leftover = ItemHandlerHelper.insertItem(p.handler, leftover, false);
                }
                if (!leftover.isEmpty()) {
                    this.dropAtSelf(leftover);
                }
            }
        }
    }

    /** OG behavior: container items (empty buckets) go back where the ingredient came from. */
    private void returnRemainder(StackPointer p, ItemStack remainder) {
        if (p.handler == null) {
            ItemStack current = this.grid.get(p.index);
            if (current.isEmpty()) {
                this.grid.set(p.index, remainder);
                return;
            }
            if ((ItemStack.isSame(current, remainder) && ItemStack.tagMatches(current, remainder))
                    && current.getCount() + remainder.getCount() <= current.getMaxStackSize()) {
                current.grow(remainder.getCount());
                return;
            }
            this.dropAtSelf(remainder);
        } else {
            ItemStack leftover = p.handler.insertItem(p.index, remainder, false);
            if (!leftover.isEmpty()) {
                leftover = ItemHandlerHelper.insertItem(p.handler, leftover, false);
            }
            if (!leftover.isEmpty()) {
                this.dropAtSelf(leftover);
            }
        }
    }

    private void dropAtSelf(ItemStack stack) {
        InventoryHelper.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(),
                this.worldPosition.getZ(), stack);
    }

    /**
     * Port of OG addItem: refill the matching grid stack with the SMALLEST
     * count first (keeps the recipe balanced). Only items already present in
     * the grid are accepted — this block is a crafter, not a chest.
     */
    public ItemStack insertBalanced(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int space = 0;
        for (ItemStack g : this.grid) {
            if (!g.isEmpty() && (ItemStack.isSame(g, stack) && ItemStack.tagMatches(g, stack))) {
                space += g.getMaxStackSize() - g.getCount();
            }
        }
        int toInsert = Math.min(space, stack.getCount());
        if (toInsert <= 0) {
            return stack;
        }
        if (!simulate) {
            for (int n = 0; n < toInsert; n++) {
                int minSlot = -1;
                int minCount = Integer.MAX_VALUE;
                for (int i = 0; i < 9; i++) {
                    ItemStack g = this.grid.get(i);
                    if (g.isEmpty() || !(ItemStack.isSame(g, stack) && ItemStack.tagMatches(g, stack))
                            || g.getCount() >= g.getMaxStackSize()) {
                        continue;
                    }
                    if (g.getCount() < minCount) {
                        minCount = g.getCount();
                        minSlot = i;
                    }
                }
                if (minSlot < 0) {
                    break;
                }
                this.grid.get(minSlot).grow(1);
            }
            this.setChanged();
        }
        ItemStack leftover = stack.copy();
        leftover.shrink(toInsert);
        return leftover.isEmpty() ? ItemStack.EMPTY : leftover;
    }

    private static final class StackPointer {
        private final IItemHandler handler;
        private final int index;
        private final ItemStack taken;

        StackPointer(IItemHandler handler, int index, ItemStack taken) {
            this.handler = handler;
            this.index = index;
            this.taken = taken;
        }

        IItemHandler handler() {
            return this.handler;
        }

        int index() {
            return this.index;
        }

        ItemStack taken() {
            return this.taken;
        }
    }

    /**
     * Automation view: one virtual slot showing the craft result. Extracting
     * performs a craft (recipe-preserving); inserting refills the grid.
     */
    public class AutoCraftHandler implements IItemHandler {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return AutoCraftingTableBlockEntity.this.findRecipe();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return AutoCraftingTableBlockEntity.this.insertBalanced(stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0) {
                return ItemStack.EMPTY;
            }
            ItemStack preview = AutoCraftingTableBlockEntity.this.findRecipe();
            // A craft is atomic: partial extraction of a multi-item result is impossible
            if (preview.isEmpty() || preview.getCount() > amount) {
                return ItemStack.EMPTY;
            }
            return AutoCraftingTableBlockEntity.this.craftOne(!simulate, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            for (ItemStack g : AutoCraftingTableBlockEntity.this.grid) {
                if (!g.isEmpty() && (ItemStack.isSame(g, stack) && ItemStack.tagMatches(g, stack))) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---- IInventory (backs the GUI grid slots) ----

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.grid) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.grid.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStackHelper.removeItem(this.grid, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStackHelper.takeItem(this.grid, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.grid.set(slot, stack);
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
        this.grid.clear();
    }

    // ---- INamedContainerProvider ----

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.crafting");
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new AutoCraftingTableMenu(id, inv, this);
    }

    // ---- persistence / caps ----

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        ItemStackHelper.saveAllItems(tag, this.grid);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.grid = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.grid);
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
        this.handler = LazyOptional.of(AutoCraftHandler::new);
    }

    @Override
    public void setRemoved() {
        if (this.level != null) {
            InventoryHelper.dropContents(this.level, this.worldPosition, this);
        }
        super.setRemoved();
    }
}
