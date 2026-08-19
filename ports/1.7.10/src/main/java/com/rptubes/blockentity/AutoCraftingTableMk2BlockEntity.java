package com.rptubes.blockentity;

import com.rptubes.menu.AutoCraftingTableMk2Menu;
import com.rptubes.registry.ModBlockEntities;
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
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import com.rptubes.util.inv.IItemHandler;

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
public class AutoCraftingTableMk2BlockEntity extends CompatTileEntity implements IInventory, InvDefaults, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    public static final int SLOT_OUTPUT = 0;
    public static final int START_RECIPE = 1;
    public static final int SIZE_RECIPE = 9;
    public static final int START_INPUT = 10;
    public static final int SIZE_INPUT = 45;
    public static final int START_OVERFLOW = START_INPUT + 36;
    public static final int INV_SIZE = START_INPUT + SIZE_INPUT;

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

    private ItemStack[] inv = new ItemStack[INV_SIZE];
    private final IItemHandler handler = new Mk2Handler();

    private IRecipe cachedRecipe;
    private boolean recipeChanged = true;

    public AutoCraftingTableMk2BlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, AutoCraftingTableMk2BlockEntity be) {
        if (!com.rptubes.util.Stacks.isEmpty(be.inv[SLOT_OUTPUT])) {
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
            if (!com.rptubes.util.Stacks.isEmpty(this.inv[k + START_RECIPE])) {
                return true;
            }
        }
        return false;
    }

    /** What the ghost template resolves to — used by the GUI's faded result preview. */
    public ItemStack findRecipe() {
        if (this.getWorld() == null) {
            return null;
        }
        InventoryCrafting matrix = this.makeMatrix(START_RECIPE);
        return net.minecraft.item.crafting.CraftingManager.getInstance().findMatchingRecipe(matrix, this.getWorld());
    }

    private InventoryCrafting makeMatrix(int start) {
        InventoryCrafting matrix = new InventoryCrafting(DUMMY_MENU, 3, 3);
        for (int k = 0; k < 9; k++) {
            matrix.setInventorySlotContents(k, this.inv[k + start] == null ? null : this.inv[k + start].copy());
        }
        return matrix;
    }

    private void cacheRecipe() {
        InventoryCrafting matrix = this.makeMatrix(START_RECIPE);
        this.cachedRecipe = null;
        // 1.7.10 findMatchingRecipe returns an ItemStack, so scan the list for the IRecipe
        for (Object o : net.minecraft.item.crafting.CraftingManager.getInstance().getRecipeList()) {
            IRecipe candidate = (IRecipe) o;
            if (candidate.matches(matrix, this.getWorld())) {
                this.cachedRecipe = candidate;
                return;
            }
        }
    }

    /** Port of OG makeOutput: map ghost slots to real input, consume, produce. */
    private void makeOutput() {
        // slotMap[ghost slot] = input slot supplying it; used[] guards double-booking
        int[] slotMap = new int[9];
        int[] used = new int[SIZE_INPUT];
        for (int k = 0; k < 9; k++) {
            ItemStack recipe = this.inv[k + START_RECIPE];
            slotMap[k] = -1;
            if (com.rptubes.util.Stacks.isEmpty(recipe)) {
                continue;
            }
            for (int i = 0; i < SIZE_INPUT; i++) {
                ItemStack input = this.inv[i + START_INPUT];
                if (com.rptubes.util.Stacks.isEmpty(input) || used[i] >= input.stackSize) {
                    continue;
                }
                if ((recipe.isItemEqual(input) && ItemStack.areItemStackTagsEqual(recipe, input))) {
                    slotMap[k] = i;
                    used[i]++;
                    break;
                }
            }
            if (slotMap[k] == -1) {
                return;
            }
        }

        InventoryCrafting matrix = new InventoryCrafting(DUMMY_MENU, 3, 3);
        for (int k = 0; k < 9; k++) {
            if (slotMap[k] != -1) {
                ItemStack one = this.inv[slotMap[k] + START_INPUT].copy();
                one.stackSize = 1;
                matrix.setInventorySlotContents(k, one);
            }
        }
        if (!this.cachedRecipe.matches(matrix, this.getWorld())) {
            return;
        }

        ItemStack result = this.cachedRecipe.getCraftingResult(matrix);
        if (com.rptubes.util.Stacks.isEmpty(result)) {
            return;
        }
        for (int k = 0; k < 9; k++) {
            if (slotMap[k] != -1) {
                this.inv[slotMap[k] + START_INPUT].splitStack(1);
                if (com.rptubes.util.Stacks.isEmpty(this.inv[slotMap[k] + START_INPUT])) {
                    this.inv[slotMap[k] + START_INPUT] = null;
                }
            }
        }
        // 1.7.10 has no IRecipe.getRemainingItems: container items come from the item itself
        for (int k = 0; k < 9; k++) {
            ItemStack inSlot = matrix.getStackInSlot(k);
            if (!com.rptubes.util.Stacks.isEmpty(inSlot) && inSlot.getItem().hasContainerItem(inSlot)) {
                ItemStack leftover = inSlot.getItem().getContainerItem(inSlot);
                if (!com.rptubes.util.Stacks.isEmpty(leftover)) {
                    this.mergeIntoOverflow(leftover.copy());
                }
            }
        }
        this.inv[SLOT_OUTPUT] = result;
        this.markDirty();
    }

    /** OG mergeStackIntoRange targeting the overflow row; drops if there's no room. */
    private void mergeIntoOverflow(ItemStack stack) {
        for (int i = START_OVERFLOW; i < INV_SIZE && !com.rptubes.util.Stacks.isEmpty(stack); i++) {
            ItemStack existing = this.inv[i];
            if (com.rptubes.util.Stacks.isEmpty(existing)) {
                this.inv[i] = stack;
                return;
            }
            if ((existing.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(existing, stack))) {
                int room = existing.getMaxStackSize() - existing.stackSize;
                int moved = Math.min(room, stack.stackSize);
                existing.stackSize += moved;
                stack.stackSize -= moved;
            }
        }
        if (!com.rptubes.util.Stacks.isEmpty(stack)) {
            com.rptubes.util.Stacks.spawnItemStack(this.getWorld(), this.getPos().getX(), this.getPos().getY(),
                    this.getPos().getZ(), stack);
        }
    }

    /** Insert into input storage: merge with matching stacks first, then empty slots. */
    public ItemStack insertInput(ItemStack stack, boolean simulate) {
        if (com.rptubes.util.Stacks.isEmpty(stack)) {
            return null;
        }
        ItemStack remaining = stack.copy();
        for (int pass = 0; pass < 2 && !com.rptubes.util.Stacks.isEmpty(remaining); pass++) {
            for (int i = START_INPUT; i < INV_SIZE && !com.rptubes.util.Stacks.isEmpty(remaining); i++) {
                ItemStack existing = this.inv[i];
                if (pass == 0) {
                    if (com.rptubes.util.Stacks.isEmpty(existing) || !(existing.isItemEqual(remaining) && ItemStack.areItemStackTagsEqual(existing, remaining))) {
                        continue;
                    }
                    int room = existing.getMaxStackSize() - existing.stackSize;
                    int moved = Math.min(room, remaining.stackSize);
                    if (moved <= 0) {
                        continue;
                    }
                    if (!simulate) {
                        existing.stackSize += moved;
                    }
                    remaining.stackSize -= moved;
                } else if (com.rptubes.util.Stacks.isEmpty(existing)) {
                    if (!simulate) {
                        this.inv[i] = remaining.copy();
                    }
                    remaining = null;
                }
            }
        }
        if (!simulate) {
            this.markInputChanged();
        }
        return remaining;
    }

    private void markInputChanged() {
        this.markDirty();
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
            return AutoCraftingTableMk2BlockEntity.this.inv[this.toInv(slot)];
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
                return null;
            }
            ItemStack existing = AutoCraftingTableMk2BlockEntity.this.inv[inv];
            if (com.rptubes.util.Stacks.isEmpty(existing) || amount <= 0) {
                return null;
            }
            int taken = Math.min(amount, existing.stackSize);
            ItemStack result = existing.copy();
            result.stackSize = taken;
            if (!simulate) {
                existing.stackSize -= taken;
                if (com.rptubes.util.Stacks.isEmpty(existing)) {
                    AutoCraftingTableMk2BlockEntity.this.inv[inv] = null;
                }
                AutoCraftingTableMk2BlockEntity.this.markInputChanged();
            }
            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            return slot != 0;
        }
    }

    // ---- IInventory (backs the GUI slots) ----

    @Override
    public int getSizeInventory() {
        return INV_SIZE;
    }

        public boolean isEmpty() {
        for (ItemStack stack : this.inv) {
            if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inv[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        this.slotChanging(slot);
        return com.rptubes.util.Stacks.getAndSplit(this.inv, slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        this.slotChanging(slot);
        return com.rptubes.util.Stacks.getAndRemove(this.inv, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.slotChanging(slot);
        this.inv[slot] = stack;
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
        java.util.Arrays.fill(this.inv, null);
    }

    // ---- InvDefaults ----

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.act_mk2");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new AutoCraftingTableMk2Menu(inv, this);
    }

    // ---- persistence / caps ----

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.inv);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inv = new ItemStack[INV_SIZE];
        com.rptubes.util.Stacks.loadAllItems(tag, this.inv);
        this.recipeChanged = true;
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        return this.handler;
    }

    public void dropContents() {
        if (this.getWorld() != null) {
            // Drop output + input storage, but never the ghost recipe templates
            for (int i = 0; i < INV_SIZE; i++) {
                if (i >= START_RECIPE && i < START_RECIPE + SIZE_RECIPE) {
                    continue;
                }
                ItemStack stack = this.inv[i];
                if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                    com.rptubes.util.Stacks.spawnItemStack(this.getWorld(), this.getPos().getX(), this.getPos().getY(),
                            this.getPos().getZ(), stack);
                }
            }
        }
    }

    @Override
    public void updateEntity() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        serverTick(this.getWorld(), this.getPos(), this);
    }
}
