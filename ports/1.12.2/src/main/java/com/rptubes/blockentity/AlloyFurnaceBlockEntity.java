package com.rptubes.blockentity;

import com.rptubes.block.AlloyFurnaceBlock;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModRecipes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IIntArray;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Port of eloraam.base.TileAlloyFurnace — 3x3 alloy grid, fuel, output.
 */
public class AlloyFurnaceBlockEntity extends TileEntity implements net.minecraft.util.ITickable, ISidedInventory, InvDefaults {
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_COUNT = 9;
    public static final int SLOT_FUEL = 9;
    public static final int SLOT_OUTPUT = 10;
    public static final int SIZE = 11;

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
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

    private final IItemHandler handlerUp = new SidedInvWrapper(this, EnumFacing.UP);
    private final IItemHandler handlerDown = new SidedInvWrapper(this, EnumFacing.DOWN);
    private final IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.NORTH);

    public AlloyFurnaceBlockEntity() {
        super();
    }

    /** Exposed for the menu's data slot sync. */
    public IIntArray getData() {
        return this.data;
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, AlloyFurnaceBlockEntity furnace) {
        boolean wasLit = furnace.isLit();
        boolean changed = false;

        if (furnace.burnTime > 0) {
            --furnace.burnTime;
        }

        AlloyRecipe recipe = furnace.findRecipe(world);
        boolean canSmelt = recipe != null && furnace.canOutput(recipe);

        if (furnace.burnTime == 0 && canSmelt) {
            ItemStack fuel = furnace.items.get(SLOT_FUEL);
            int burn = net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime(fuel);
            if (burn > 0) {
                furnace.burnTime = furnace.burnTimeTotal = burn;
                changed = true;
                if (fuel.getItem().hasContainerItem(fuel)) {
                    furnace.items.set(SLOT_FUEL, fuel.getItem().getContainerItem(fuel));
                } else {
                    fuel.shrink(1);
                    if (fuel.isEmpty()) {
                        furnace.items.set(SLOT_FUEL, ItemStack.EMPTY);
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
                ItemStack out = furnace.items.get(SLOT_OUTPUT);
                if (out.isEmpty()) {
                    furnace.items.set(SLOT_OUTPUT, result);
                } else {
                    out.grow(result.getCount());
                }
                changed = true;
            }
        } else {
            furnace.cookTime = 0;
        }

        if (wasLit != furnace.isLit()) {
            changed = true;
            world.setBlockState(pos, state.withProperty(AlloyFurnaceBlock.LIT, furnace.isLit()), 3);
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
        if (result.isEmpty()) {
            return false;
        }
        ItemStack out = this.items.get(SLOT_OUTPUT);
        if (out.isEmpty()) {
            return true;
        }
        if (!(out.isItemEqual(result) && ItemStack.areItemStackTagsEqual(out, result))) {
            return false;
        }
        return out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.alloy_furnace");
    }

    public Container createMenu(InventoryPlayer inventory) {
        return new AlloyFurnaceMenu(inventory, this);
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return ItemStackHelper.getAndSplit(this.items, slot, amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return ItemStackHelper.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null
                && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq(this.pos.getX() + 0.5,
                this.pos.getY() + 0.5,
                this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return new int[]{SLOT_OUTPUT};
        }
        if (side == EnumFacing.UP) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        }
        return new int[]{SLOT_FUEL};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        tag.setInteger("BurnTime", this.burnTime);
        tag.setInteger("BurnTimeTotal", this.burnTimeTotal);
        tag.setInteger("CookTime", this.cookTime);
        tag.setInteger("CookTimeTotal", this.cookTimeTotal);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.burnTime = tag.getInteger("BurnTime");
        this.burnTimeTotal = tag.getInteger("BurnTimeTotal");
        this.cookTime = tag.getInteger("CookTime");
        this.cookTimeTotal = tag.getInteger("CookTimeTotal");
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null)
                || super.hasCapability(cap, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null) {
            if (side == EnumFacing.UP) {
                return (T) this.handlerUp;
            }
            if (side == EnumFacing.DOWN) {
                return (T) this.handlerDown;
            }
            return (T) this.handlerSide;
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        serverTick(this.world, this.pos, this.world.getBlockState(this.pos), this);
    }
}
