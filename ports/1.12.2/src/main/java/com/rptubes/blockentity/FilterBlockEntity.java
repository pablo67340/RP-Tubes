package com.rptubes.blockentity;

import com.rptubes.menu.FilterMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeItem;
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
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Port of TileFilter — stack extract + optional filter slots + paint color.
 */
public class FilterBlockEntity extends TransposerBlockEntity implements ISidedInventory, InvDefaults {
    private NonNullList<ItemStack> filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
    private MachineLib.FilterMap filterMap;
    public byte color = 0;
    private final IItemHandler handler = new SidedInvWrapper(this, EnumFacing.NORTH);
    protected final MachineEnergy energy = new MachineEnergy();

    public FilterBlockEntity() {
        super();
    }

    private void regenFilterMap() {
        ItemStack[] arr = this.filterSlots.toArray(new ItemStack[0]);
        this.filterMap = MachineLib.makeFilterMap(arr);
    }

    private MachineLib.FilterMap map() {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        return this.filterMap;
    }

    @Override
    protected void addToBuffer(ItemStack stack) {
        this.buffer.addNewColor(stack, this.color & 0xFF);
    }

    @Override
    protected ItemStack extractFrom(BlockPos src, EnumFacing extractSide) {
        MachineLib.FilterMap map = this.map();
        if (map.size() == 0) {
            return MachineLib.collectOneStack(this.world, src, extractSide, ItemStack.EMPTY);
        }
        int slot = MachineLib.matchAnyStack(map, this.world, src, extractSide);
        if (slot < 0) {
            return ItemStack.EMPTY;
        }
        return MachineLib.collectOneStack(this.world, src, extractSide, this.filterSlots.get(slot).copy());
    }

    @Override
    protected boolean suckFilter(ItemStack stack) {
        MachineLib.FilterMap map = this.map();
        return map.size() == 0 || map.containsKey(stack);
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        EnumFacing back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 1) {
            MachineLib.FilterMap map = this.map();
            if (map.size() > 0 && !map.containsKey(ti.item)) {
                return false;
            }
        }
        return super.tubeItemEnter(side, state, ti);
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        EnumFacing back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 1) {
            MachineLib.FilterMap map = this.map();
            if (map.size() > 0 && !map.containsKey(ti.item)) {
                return false;
            }
        }
        return super.tubeItemCanEnter(side, state, ti);
    }

    @Override
    protected boolean payForExtract(boolean mustHaveEnergy) {
        return this.energy.pay(mustHaveEnergy);
    }

    @Override
    protected void tryEnergyAutoExtract() {
        if (!this.energy.canAutoExtract()) {
            return;
        }
        if (!this.energy.pay(true)) {
            return;
        }
        this.setActive(true);
        this.startExtract();
        this.tickCooldown = 10;
        this.markDirty();
    }

    public boolean applyColor(int dyeColor) {
        // 1-16 dye colors; 0 = unpainted
        byte next = (byte) ((dyeColor % 16) + 1);
        if (this.color == next) {
            return false;
        }
        this.color = next;
        this.markDirty();
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.filterSlots.get(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack result = ItemStackHelper.getAndSplit(this.filterSlots, slot, amount);
        if (!result.isEmpty()) {
            this.filterMap = null;
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        this.filterMap = null;
        return ItemStackHelper.getAndRemove(this.filterSlots, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.filterSlots.set(slot, stack);
        this.filterMap = null;
        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null
                && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5,
                this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        this.filterSlots.clear();
        this.filterMap = null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        EnumFacing face = this.getFacing();
        if (side == face || side == face.getOpposite()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.filter");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new FilterMenu(inv, this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.filterSlots);
        tag.setByte("color", this.color);
        this.energy.write(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.filterSlots);
        this.color = tag.getByte("color");
        this.energy.read(tag);
        this.filterMap = null;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || this.energy.isEnergyCap(cap)
                || super.hasCapability(cap, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.handler;
        }
        T energy = this.energy.cast(cap, side);
        if (energy != null) {
            return energy;
        }
        return super.getCapability(cap, side);
    }

    public void dropContents() {
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty() && this.world != null) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(this.world,
                        this.pos.getX(), this.pos.getY(), this.pos.getZ(), stack);
            }
        }
    }
}
