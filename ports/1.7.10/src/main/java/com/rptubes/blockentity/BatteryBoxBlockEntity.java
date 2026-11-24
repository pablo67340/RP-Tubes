package com.rptubes.blockentity;

import com.rptubes.block.BatteryBoxBlock;
import com.rptubes.item.BatteryItem;
import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBlockEntity;
import com.rptubes.power.PowerBuffer;
import com.rptubes.util.IIntArray;
import com.rptubes.util.Stacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Port of TileBatteryBox: 6000 RP2 units (600,000 RF). Takes energy from any side and
 * gives it to every neighbour except other Battery Boxes, up to 10 units (1000 RF) per
 * tick; a redstone signal stops it giving. Slot 0 charges an item from storage, slot 1
 * drains an item into storage, 25 units (2500 RF) per tick each.
 */
public class BatteryBoxBlockEntity extends PowerBlockEntity implements DropsContents, ISidedInventory, InvDefaults {
    public static final int SLOT_CHARGE = 0;
    public static final int SLOT_DRAIN = 1;
    public static final long CAPACITY = 6000L * PowerBuffer.FE_PER_UNIT;
    public static final long MAX_IO = 10L * PowerBuffer.FE_PER_UNIT;
    private static final int[] TOP = {SLOT_CHARGE};
    private static final int[] BOTTOM = {SLOT_DRAIN};
    private static final int[] NONE = {};

    private ItemStack[] items = new ItemStack[2];
    private long lastStored;
    private int inRate;
    private int outRate;

    /** Server side reads live values; the client menu stores what the server sends. */
    private final IIntArray data = new IIntArray() {
        private final int[] synced = new int[3];

        @Override
        public int get(int index) {
            BatteryBoxBlockEntity be = BatteryBoxBlockEntity.this;
            if (be.worldObj == null || be.worldObj.isRemote) {
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

        boolean powered = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
        this.buffer.setExtractEnabled(!powered);
        if (!powered) {
            outflow += this.pushToNeighbours(MAX_IO);
        }

        this.inRate = (int) Math.min(MAX_IO, inflow);
        this.outRate = (int) Math.min(MAX_IO, outflow);
        this.lastStored = this.buffer.getStored();

        int charge = (int) (this.buffer.getStored() * 8 / CAPACITY);
        if (this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) instanceof BatteryBoxBlock
                && this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) != charge) {
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, charge, 2);
        }
    }

    @Override
    protected boolean canPushTo(ForgeDirection dir) {
        return !(this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY,
                this.zCoord + dir.offsetZ) instanceof BatteryBoxBlockEntity);
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
    public ItemStack getStackInSlot(int slot) {
        return this.items[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack removed = Stacks.getAndSplit(this.items, slot, amount);
        if (!Stacks.isEmpty(removed)) {
            this.markDirty();
        }
        return removed;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return Stacks.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj != null
                && this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return side == 1 ? TOP : side == 0 ? BOTTOM : NONE;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return !(stack.getItem() instanceof BatteryItem) || BatteryItem.getCharge(stack) == 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        Stacks.saveAllItems(tag, this.items);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = new ItemStack[2];
        Stacks.loadAllItems(tag, this.items);
        this.lastStored = this.buffer.getStored();
    }

    @Override
    public void dropContents() {
        if (this.worldObj != null) {
            Stacks.dropAll(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.items);
        }
    }
}
