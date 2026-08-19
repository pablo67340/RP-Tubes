package com.rptubes.blockentity;

import com.rptubes.block.AlloyFurnaceBlock;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModRecipes;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IIntArray;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import com.rptubes.util.inv.IItemHandler;
import com.rptubes.util.inv.SidedInvWrapper;

/**
 * Port of eloraam.base.TileAlloyFurnace — 3x3 alloy grid, fuel, output.
 */
public class AlloyFurnaceBlockEntity extends CompatTileEntity implements ISidedInventory, InvDefaults, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_COUNT = 9;
    public static final int SLOT_FUEL = 9;
    public static final int SLOT_OUTPUT = 10;
    public static final int SIZE = 11;

    private ItemStack[] items = new ItemStack[SIZE];
    private int burnTime;
    private int burnTimeTotal;
    private int cookTime;
    private int cookTimeTotal = 200;

    private final IIntArray data = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return burnTime;
                case 1: return burnTimeTotal;
                case 2: return cookTime;
                case 3: return cookTimeTotal;
                default: return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: burnTime = value; break;
                case 1: burnTimeTotal = value; break;
                case 2: cookTime = value; break;
                case 3: cookTimeTotal = value; break;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private final IItemHandler handlerUp = new SidedInvWrapper(this, ForgeDirection.UP);
    private final IItemHandler handlerDown = new SidedInvWrapper(this, ForgeDirection.DOWN);
    private final IItemHandler handlerSide = new SidedInvWrapper(this, ForgeDirection.NORTH);

    public AlloyFurnaceBlockEntity() {
        super();
    }

    /** Exposed for the menu's data slot sync. */
    public IIntArray getData() {
        return this.data;
    }

    public static void serverTick(World world, BlockPos pos, AlloyFurnaceBlockEntity furnace) {
        boolean wasLit = furnace.isLit();
        boolean changed = false;

        if (furnace.burnTime > 0) {
            --furnace.burnTime;
        }

        AlloyRecipe recipe = furnace.findRecipe(world);
        boolean canSmelt = recipe != null && furnace.canOutput(recipe);

        if (furnace.burnTime == 0 && canSmelt) {
            ItemStack fuel = furnace.items[SLOT_FUEL];
            int burn = net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime(fuel);
            if (burn > 0) {
                furnace.burnTime = furnace.burnTimeTotal = burn;
                changed = true;
                if (fuel.getItem().hasContainerItem(fuel)) {
                    furnace.items[SLOT_FUEL] = fuel.getItem().getContainerItem(fuel);
                } else {
                    fuel.splitStack(1);
                    if (com.rptubes.util.Stacks.isEmpty(fuel)) {
                        furnace.items[SLOT_FUEL] = null;
                    }
                }
            }
        }

        if (furnace.isLit() && canSmelt) {
            furnace.cookTimeTotal = recipe.getCookTime();
            ++furnace.cookTime;
            if (furnace.cookTime >= furnace.cookTimeTotal) {
                furnace.cookTime = 0;
                recipe.consumeInputs(furnace);
                ItemStack result = recipe.assemble();
                ItemStack out = furnace.items[SLOT_OUTPUT];
                if (com.rptubes.util.Stacks.isEmpty(out)) {
                    furnace.items[SLOT_OUTPUT] = result;
                } else {
                    out.stackSize += result.stackSize;
                }
                changed = true;
            }
        } else {
            furnace.cookTime = 0;
        }

        if (wasLit != furnace.isLit()) {
            changed = true;
                com.rptubes.block.AlloyFurnaceBlock.setLit(world, pos.getX(), pos.getY(), pos.getZ(), furnace.isLit());
        }
        if (changed) {
            furnace.markDirty();
        }
    }

    private boolean isLit() {
        return this.burnTime > 0;
    }

    private AlloyRecipe findRecipe(World world) {
        return ModRecipes.findAlloyRecipe(this);
    }

    private boolean canOutput(AlloyRecipe recipe) {
        ItemStack result = recipe.getResultItem();
        if (com.rptubes.util.Stacks.isEmpty(result)) {
            return false;
        }
        ItemStack out = this.items[SLOT_OUTPUT];
        if (com.rptubes.util.Stacks.isEmpty(out)) {
            return true;
        }
        if (!(out.isItemEqual(result) && ItemStack.areItemStackTagsEqual(out, result))) {
            return false;
        }
        return out.stackSize + result.stackSize <= out.getMaxStackSize();
    }

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.alloy_furnace");
    }

    public Container createMenu(InventoryPlayer inventory) {
        return new AlloyFurnaceMenu(inventory, this);
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

        public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.items[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return com.rptubes.util.Stacks.getAndSplit(this.items, slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return com.rptubes.util.Stacks.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
        if (stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getWorld() != null
                && this.getWorld().getTileEntity(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) == this
                && player.getDistanceSq(this.getPos().getX() + 0.5,
                this.getPos().getY() + 0.5,
                this.getPos().getZ() + 0.5) <= 64.0;
    }

        public void clear() {
        java.util.Arrays.fill(this.items, null);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == ForgeDirection.DOWN.ordinal()) {
            return new int[]{SLOT_OUTPUT};
        }
        if (side == ForgeDirection.UP.ordinal()) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        }
        return new int[]{SLOT_FUEL};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return index == SLOT_OUTPUT;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == SLOT_OUTPUT) {
            return false;
        }
        if (index == SLOT_FUEL) {
            return net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime(stack) > 0;
        }
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.items);
        tag.setInteger("BurnTime", this.burnTime);
        tag.setInteger("BurnTimeTotal", this.burnTimeTotal);
        tag.setInteger("CookTime", this.cookTime);
        tag.setInteger("CookTimeTotal", this.cookTimeTotal);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = new ItemStack[SIZE];
        com.rptubes.util.Stacks.loadAllItems(tag, this.items);
        this.burnTime = tag.getInteger("BurnTime");
        this.burnTimeTotal = tag.getInteger("BurnTimeTotal");
        this.cookTime = tag.getInteger("CookTime");
        this.cookTimeTotal = tag.getInteger("CookTimeTotal");
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        if (side == ForgeDirection.UP) {
            return this.handlerUp;
        }
        if (side == ForgeDirection.DOWN) {
            return this.handlerDown;
        }
        return this.handlerSide;
    }

    public void dropContents() {
        if (this.getWorld() != null) {
            com.rptubes.util.Stacks.dropInventory(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, this);
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
