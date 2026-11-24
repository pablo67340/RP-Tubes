package com.rptubes.blockentity;

import com.rptubes.block.BatteryBoxBlock;
import com.rptubes.item.BatteryItem;
import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBlockEntity;
import com.rptubes.power.PowerBuffer;
import com.rptubes.util.IIntArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Port of TileBatteryBox: 6000 RP2 units (600,000 FE). Takes energy from any side and
 * gives it to every neighbour except other Battery Boxes, up to 10 units (1000 FE) per
 * tick; a redstone signal stops it giving. Slot 0 charges an item from storage, slot 1
 * drains an item into storage, 25 units (2500 FE) per tick each.
 */
public class BatteryBoxBlockEntity extends PowerBlockEntity implements DropsContents, ISidedInventory, InvDefaults {
    public static final int SLOT_CHARGE = 0;
    public static final int SLOT_DRAIN = 1;
    public static final long CAPACITY = 6000L * PowerBuffer.FE_PER_UNIT;
    public static final long MAX_IO = 10L * PowerBuffer.FE_PER_UNIT;
    private static final int[] TOP = {SLOT_CHARGE};
    private static final int[] BOTTOM = {SLOT_DRAIN};
    private static final int[] NONE = {};

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private long lastStored;
    private int inRate;
    private int outRate;
    private final IItemHandler[] handlers = new IItemHandler[6];

    {
        for (EnumFacing face : EnumFacing.VALUES) {
            this.handlers[face.ordinal()] = new SidedInvWrapper(this, face);
        }
    }

    /** Server side reads live values; the client menu stores what the server sends. */
    private final IIntArray data = new IIntArray() {
        private final int[] synced = new int[3];

        @Override
        public int get(int index) {
            BatteryBoxBlockEntity be = BatteryBoxBlockEntity.this;
            if (be.world == null || be.world.isRemote) {
                return index >= 0 && index < 3 ? this.synced[index] : 0;
            }
            switch (index) {
                case 0:
                    return (int) (be.buffer.getStored() / PowerBuffer.FE_PER_UNIT);
                case 1:
                    return be.inRate;
                case 2:
                    return be.outRate;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            if (index >= 0 && index < 3) {
                this.synced[index] = value;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public BatteryBoxBlockEntity() {
        super(CAPACITY, MAX_IO, MAX_IO);
    }

    public IIntArray getData() {
        return this.data;
    }

    @Override
    protected void serverTick() {
        long external = this.buffer.getStored() - this.lastStored;
        long inflow = Math.max(0, external);
        long outflow = Math.max(0, -external);

        long stored = this.buffer.getStored();
        if (stored > 0) {
            this.buffer.drain(ItemPower.charge(this, SLOT_CHARGE, Math.min(BatteryItem.TRANSFER, stored)));
        }
        long room = CAPACITY - this.buffer.getStored();
        if (room > 0) {
            this.buffer.generate(ItemPower.drain(this, SLOT_DRAIN, Math.min(BatteryItem.TRANSFER, room)));
        }

        boolean powered = this.world.isBlockPowered(this.pos);
        this.buffer.setExtractEnabled(!powered);
        if (!powered) {
            outflow += this.pushToNeighbours(MAX_IO);
        }

        this.inRate = (int) Math.min(MAX_IO, inflow);
        this.outRate = (int) Math.min(MAX_IO, outflow);
        this.lastStored = this.buffer.getStored();

        IBlockState state = this.world.getBlockState(this.pos);
        int charge = (int) (this.buffer.getStored() * 8 / CAPACITY);
        if (state.getBlock() instanceof BatteryBoxBlock && state.getValue(BatteryBoxBlock.CHARGE) != charge) {
            this.world.setBlockState(this.pos, state.withProperty(BatteryBoxBlock.CHARGE, charge), 2);
        }
    }

    @Override
    protected boolean canPushTo(EnumFacing dir) {
        return !(this.world.getTileEntity(this.pos.offset(dir)) instanceof BatteryBoxBlockEntity);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.battery_box");
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
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
        ItemStack removed = ItemStackHelper.getAndSplit(this.items, slot, amount);
        if (!removed.isEmpty()) {
            this.markDirty();
        }
        return removed;
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
                && player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5,
                this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? TOP : side == EnumFacing.DOWN ? BOTTOM : NONE;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return !(stack.getItem() instanceof BatteryItem) || BatteryItem.getCharge(stack) == 0;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.handlers[side == null ? 0 : side.ordinal()];
        }
        return super.getCapability(cap, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.lastStored = this.buffer.getStored();
    }

    @Override
    public void dropContents() {
        if (this.world != null) {
            InventoryHelper.dropInventoryItems(this.world, this.pos, this);
        }
    }
}
