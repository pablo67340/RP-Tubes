package com.rptubes.blockentity;

import com.rptubes.menu.AutoCraftingTableMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import com.rptubes.util.inv.IItemHandler;
import com.rptubes.util.inv.Handlers;

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
public class AutoCraftingTableBlockEntity extends CompatTileEntity implements IInventory, InvDefaults, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    /** InventoryCrafting demands a menu; recipe lookups never touch it. */
    private static final Container DUMMY_MENU = new Container() {
        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return false;
        }
        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int index) {
            return null;
        }
    };

    private ItemStack[] grid = new ItemStack[9];
    private final IItemHandler handler = new AutoCraftHandler();

    public AutoCraftingTableBlockEntity() {
        super();
    }

    /** What the current grid would craft, without consuming anything. */
    public ItemStack findRecipe() {
        if (this.getWorld() == null) {
            return null;
        }
        InventoryCrafting matrix = new InventoryCrafting(DUMMY_MENU, 3, 3);
        for (int i = 0; i < 9; i++) {
            matrix.setInventorySlotContents(i, this.grid[i] == null ? null : this.grid[i].copy());
        }
        // 1.7.10 findMatchingRecipe returns the crafted result directly
        return net.minecraft.item.crafting.CraftingManager.getInstance().findMatchingRecipe(matrix, this.getWorld());
    }

    /**
     * Port of OG extractItem(doRemove, removeRecipe): perform one craft.
     * removeRecipe=false (automation) leaves the last item of each ingredient
     * in the grid, pulling replacements from adjacent inventories.
     * removeRecipe=true (GUI take) consumes grid items directly.
     * doRemove=false simulates: ingredients are taken then rolled back.
     */
    public ItemStack craftOne(boolean doRemove, boolean removeRecipe) {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return null;
        }
        InventoryCrafting matrix = new InventoryCrafting(DUMMY_MENU, 3, 3);
        List<StackPointer> pointers = new ArrayList<>();
        StackPointer[] byMatrixSlot = new StackPointer[9];
        int itemsToLeave = removeRecipe ? 0 : 1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.grid[i];
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                continue;
            }
            StackPointer pointer;
            if (stack.stackSize <= itemsToLeave) {
                pointer = this.takeNearbyItem(stack);
                if (pointer == null) {
                    this.rollback(pointers);
                    return null;
                }
            } else {
                ItemStack taken = stack.splitStack(1);
                pointer = new StackPointer(null, i, taken);
            }
            pointers.add(pointer);
            byMatrixSlot[i] = pointer;
            matrix.setInventorySlotContents(i, pointer.taken.copy());
        }

        ItemStack result = net.minecraft.item.crafting.CraftingManager.getInstance()
                .findMatchingRecipe(matrix, this.getWorld());
        if (com.rptubes.util.Stacks.isEmpty(result)) {
            this.rollback(pointers);
            return null;
        }
        if (!doRemove) {
            this.rollback(pointers);
            return result;
        }

        // 1.7.10 has no IRecipe.getRemainingItems: container items come from the item itself
        for (int i = 0; i < 9; i++) {
            ItemStack inSlot = matrix.getStackInSlot(i);
            if (!com.rptubes.util.Stacks.isEmpty(inSlot) && inSlot.getItem().hasContainerItem(inSlot)) {
                ItemStack remainder = inSlot.getItem().getContainerItem(inSlot);
                if (!com.rptubes.util.Stacks.isEmpty(remainder) && byMatrixSlot[i] != null) {
                    this.returnRemainder(byMatrixSlot[i], remainder);
                }
            }
        }
        this.markDirty();
        return result;
    }

    /**
     * OG getNearbyItem: search all six neighbors (plain inventories only —
     * never tubes or other crafting tables) for one matching ingredient.
     */
    private StackPointer takeNearbyItem(ItemStack want) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockPos npos = this.getPos().offset(dir);
            TileEntity nbe = this.getWorld().getTileEntity(npos.getX(), npos.getY(), npos.getZ());
            if (nbe == null || nbe instanceof ITubeConnectable || nbe instanceof AutoCraftingTableBlockEntity) {
                continue;
            }
            IItemHandler neighbor =
                    com.rptubes.util.inv.Handlers.wrap(nbe, dir.getOpposite());
            if (neighbor == null) {
                continue;
            }
            for (int s = 0; s < neighbor.getSlots(); s++) {
                ItemStack inSlot = neighbor.getStackInSlot(s);
                if (com.rptubes.util.Stacks.isEmpty(inSlot)
                        || !(inSlot.isItemEqual(want) && ItemStack.areItemStackTagsEqual(inSlot, want))) {
                    continue;
                }
                ItemStack taken = neighbor.extractItem(s, 1, false);
                if (com.rptubes.util.Stacks.isEmpty(taken)) {
                    continue;
                }
                if (!(taken.isItemEqual(want) && ItemStack.areItemStackTagsEqual(taken, want))) {
                    Handlers.insertItem(neighbor, taken, false);
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
                ItemStack current = this.grid[p.index];
                if (com.rptubes.util.Stacks.isEmpty(current)) {
                    this.grid[p.index] = p.taken;
                } else {
                    current.stackSize += p.taken.stackSize;
                }
            } else {
                ItemStack leftover = p.handler.insertItem(p.index, p.taken, false);
                if (!com.rptubes.util.Stacks.isEmpty(leftover)) {
                    leftover = Handlers.insertItem(p.handler, leftover, false);
                }
                if (!com.rptubes.util.Stacks.isEmpty(leftover)) {
                    this.dropAtSelf(leftover);
                }
            }
        }
    }

    /** OG behavior: container items (empty buckets) go back where the ingredient came from. */
    private void returnRemainder(StackPointer p, ItemStack remainder) {
        if (p.handler == null) {
            ItemStack current = this.grid[p.index];
            if (com.rptubes.util.Stacks.isEmpty(current)) {
                this.grid[p.index] = remainder;
                return;
            }
            if ((current.isItemEqual(remainder) && ItemStack.areItemStackTagsEqual(current, remainder))
                    && current.stackSize + remainder.stackSize <= current.getMaxStackSize()) {
                current.stackSize += remainder.stackSize;
                return;
            }
            this.dropAtSelf(remainder);
        } else {
            ItemStack leftover = p.handler.insertItem(p.index, remainder, false);
            if (!com.rptubes.util.Stacks.isEmpty(leftover)) {
                leftover = Handlers.insertItem(p.handler, leftover, false);
            }
            if (!com.rptubes.util.Stacks.isEmpty(leftover)) {
                this.dropAtSelf(leftover);
            }
        }
    }

    private void dropAtSelf(ItemStack stack) {
        com.rptubes.util.Stacks.spawnItemStack(this.getWorld(), this.getPos().getX(), this.getPos().getY(),
                this.getPos().getZ(), stack);
    }

    /**
     * Port of OG addItem: refill the matching grid stack with the SMALLEST
     * count first (keeps the recipe balanced). Only items already present in
     * the grid are accepted — this block is a crafter, not a chest.
     */
    public ItemStack insertBalanced(ItemStack stack, boolean simulate) {
        if (com.rptubes.util.Stacks.isEmpty(stack)) {
            return null;
        }
        int space = 0;
        for (ItemStack g : this.grid) {
            if (!com.rptubes.util.Stacks.isEmpty(g) && (g.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(g, stack))) {
                space += g.getMaxStackSize() - g.stackSize;
            }
        }
        int toInsert = Math.min(space, stack.stackSize);
        if (toInsert <= 0) {
            return stack;
        }
        if (!simulate) {
            for (int n = 0; n < toInsert; n++) {
                int minSlot = -1;
                int minCount = Integer.MAX_VALUE;
                for (int i = 0; i < 9; i++) {
                    ItemStack g = this.grid[i];
                    if (com.rptubes.util.Stacks.isEmpty(g) || !(g.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(g, stack))
                            || g.stackSize >= g.getMaxStackSize()) {
                        continue;
                    }
                    if (g.stackSize < minCount) {
                        minCount = g.stackSize;
                        minSlot = i;
                    }
                }
                if (minSlot < 0) {
                    break;
                }
                this.grid[minSlot].stackSize += 1;
            }
            this.markDirty();
        }
        ItemStack leftover = stack.copy();
        leftover.stackSize -= toInsert;
        return com.rptubes.util.Stacks.isEmpty(leftover) ? null : leftover;
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
                return null;
            }
            ItemStack preview = AutoCraftingTableBlockEntity.this.findRecipe();
            // A craft is atomic: partial extraction of a multi-item result is impossible
            if (com.rptubes.util.Stacks.isEmpty(preview) || preview.stackSize > amount) {
                return null;
            }
            return AutoCraftingTableBlockEntity.this.craftOne(!simulate, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            for (ItemStack g : AutoCraftingTableBlockEntity.this.grid) {
                if (!com.rptubes.util.Stacks.isEmpty(g) && (g.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(g, stack))) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---- IInventory (backs the GUI grid slots) ----

    @Override
    public int getSizeInventory() {
        return 9;
    }

        public boolean isEmpty() {
        for (ItemStack stack : this.grid) {
            if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.grid[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return com.rptubes.util.Stacks.getAndSplit(this.grid, slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return com.rptubes.util.Stacks.getAndRemove(this.grid, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.grid[slot] = stack;
        this.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getWorld() != null
                && this.getWorld().getTileEntity(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) == this
                && player.getDistanceSq(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5,
                this.getPos().getZ() + 0.5) <= 64.0;
    }

        public void clear() {
        java.util.Arrays.fill(this.grid, null);
    }

    // ---- InvDefaults ----

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("container.crafting");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new AutoCraftingTableMenu(inv, this);
    }

    // ---- persistence / caps ----

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.grid);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.grid = new ItemStack[9];
        com.rptubes.util.Stacks.loadAllItems(tag, this.grid);
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        return this.handler;
    }

    public void dropContents() {
        if (this.getWorld() != null) {
            com.rptubes.util.Stacks.dropInventory(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, this);
        }
    }
}
