package com.rptubes.blockentity;

import com.rptubes.menu.AutoCraftingTableMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerHelper;
import com.rptubes.tube.MachineLib;
import org.jetbrains.annotations.NotNull;

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
 * normal crafting table and may consume the grid entirely. Container items
 * (buckets etc.) are returned where the ingredient came from.
 */
public class AutoCraftingTableBlockEntity extends BlockEntity implements Container, MenuProvider, com.rptubes.inventory.ItemHandlerProvider{
    private NonNullList<ItemStack> grid = NonNullList.withSize(9, ItemStack.EMPTY);
    private final IItemHandler handler = new AutoCraftHandler();

    public AutoCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTO_CRAFTING_TABLE, pos, state);
    }

    /** What the current grid would craft, without consuming anything. */
    public ItemStack findRecipe() {
        if (this.level == null || this.level.getServer() == null) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> items = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            items.add(this.grid.get(i).copy());
        }
        CraftingInput input = CraftingInput.of(3, 3, items);
        return this.level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, this.level)
                .map(r -> r.value().assemble(input, this.level.registryAccess()))
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
        if (this.level == null || this.level.isClientSide()) {
            return ItemStack.EMPTY;
        }
        NonNullList<ItemStack> matrixItems = NonNullList.withSize(9, ItemStack.EMPTY);
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
            matrixItems.set(i, pointer.taken.copy());
        }

        CraftingInput.Positioned positioned = CraftingInput.ofPositioned(3, 3, matrixItems);
        CraftingInput input = positioned.input();
        Optional<net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe>> recipe =
                this.level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, this.level);
        if (recipe.isEmpty()) {
            this.rollback(pointers);
            return ItemStack.EMPTY;
        }
        ItemStack result = recipe.get().value().assemble(input, this.level.registryAccess());
        if (result.isEmpty() || !doRemove) {
            this.rollback(pointers);
            return result;
        }

        NonNullList<ItemStack> remaining = recipe.get().value().getRemainingItems(input);
        // CraftingInput trims empty edges; map trimmed indices back to the 3x3 grid
        for (int i = 0; i < remaining.size(); i++) {
            ItemStack remainder = remaining.get(i);
            if (remainder.isEmpty()) {
                continue;
            }
            int row = i / input.width() + positioned.top();
            int col = i % input.width() + positioned.left();
            int gridIndex = row * 3 + col;
            if (gridIndex < 9 && byMatrixSlot[gridIndex] != null) {
                this.returnRemainder(byMatrixSlot[gridIndex], remainder);
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
            BlockEntity nbe = this.level.getBlockEntity(npos);
            if (nbe == null || nbe instanceof ITubeConnectable || nbe instanceof AutoCraftingTableBlockEntity) {
                continue;
            }
            IItemHandler neighbor =
                    MachineLib.getHandler(this.level, this.worldPosition.relative(dir), dir.getOpposite());
            if (neighbor == null) {
                continue;
            }
            for (int s = 0; s < neighbor.getSlots(); s++) {
                if (!ItemStack.isSameItemSameComponents(neighbor.getStackInSlot(s), want)) {
                    continue;
                }
                ItemStack taken = neighbor.extractItem(s, 1, false);
                if (taken.isEmpty()) {
                    continue;
                }
                if (!ItemStack.isSameItemSameComponents(taken, want)) {
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
            if (ItemStack.isSameItemSameComponents(current, remainder)
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
        Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(),
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
            if (!g.isEmpty() && ItemStack.isSameItemSameComponents(g, stack)) {
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
                    if (g.isEmpty() || !ItemStack.isSameItemSameComponents(g, stack)
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

    private record StackPointer(IItemHandler handler, int index, ItemStack taken) {
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
        public @NotNull ItemStack getStackInSlot(int slot) {
            return AutoCraftingTableBlockEntity.this.findRecipe();
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return AutoCraftingTableBlockEntity.this.insertBalanced(stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
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
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            for (ItemStack g : AutoCraftingTableBlockEntity.this.grid) {
                if (!g.isEmpty() && ItemStack.isSameItemSameComponents(g, stack)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---- Container (backs the GUI grid slots) ----

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
        return ContainerHelper.removeItem(this.grid, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.grid, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.grid.set(slot, stack);
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
        this.grid.clear();
    }

    // ---- MenuProvider ----

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crafting");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AutoCraftingTableMenu(id, inv, this);
    }

    // ---- persistence / caps ----

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.grid);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.grid = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.grid);
    }


    @Override
    public void setRemoved() {
        // Grid drops handled by the default preRemoveSideEffects (Container)
        super.setRemoved();
    }

    @Override
    public IItemHandler getItemHandler(Direction side) {
        return this.handler;
    }
}
