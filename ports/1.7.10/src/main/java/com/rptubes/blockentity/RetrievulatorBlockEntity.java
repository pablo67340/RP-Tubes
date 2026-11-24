package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeItem;
import com.rptubes.util.Stacks;
import com.rptubes.util.inv.IItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Port of Tubestuff's Retrievulator. Sits on a Retriever's output and keeps the
 * inventory on its own output topped up to the counts in the T grid, by moving the
 * matching R grid stacks into the Retriever's filter and pulsing it.
 */
public class RetrievulatorBlockEntity extends CompatTileEntity implements DropsContents, ITubeConnectable, ISidedInventory, InvDefaults {
    public static final int GRID = 9;
    public static final int SLOT_BUFFER = 9;
    public static final int SIZE = 10;
    public static final int MAX_TARGET = 127;
    private static final int MIN_DELAY = 10;
    private static final int MAX_DELAY = 400;
    private static final int SETTLE = 4;
    private static final int[] NO_SLOTS = new int[0];

    // R grid (0-8) and the incoming buffer (9); real items
    private ItemStack[] items = new ItemStack[SIZE];
    // T grid: single-item templates with separate counts, matching the modern versions
    private ItemStack[] targets = new ItemStack[GRID];
    private int[] targetCounts = new int[GRID];
    private final IInventory targetView = new TargetView();
    private int delay = MIN_DELAY;
    private int countdown;
    private boolean inFlight;
    private int arrivals;
    private int pulseTicks;

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        if (this.pulseTicks > 0 && --this.pulseTicks == 0) {
            this.setActive(false);
        }
        ForgeDirection face = this.getFacing();
        this.drainBuffer(face);

        ForgeDirection back = face.getOpposite();
        TileEntity behind = this.worldObj.getTileEntity(this.xCoord + back.offsetX, this.yCoord + back.offsetY,
                this.zCoord + back.offsetZ);
        if (!(behind instanceof FilterBlockEntity)) {
            return;
        }
        FilterBlockEntity filter = (FilterBlockEntity) behind;
        int[] have = this.countStock(face);
        if (have == null) {
            return;
        }
        // Each square must hold its stack in exactly one place, R grid or filter
        for (int i = 0; i < GRID; i++) {
            if (this.targets[i] == null) {
                continue;
            }
            if ((this.items[i] == null) == (filter.getStackInSlot(i) == null)) {
                return;
            }
        }
        boolean wanted = false;
        for (int i = 0; i < GRID; i++) {
            if (this.targets[i] == null) {
                continue;
            }
            ItemStack r = this.items[i];
            ItemStack f = filter.getStackInSlot(i);
            if (have[i] < this.targetCounts[i]) {
                wanted = true;
                if (r != null) {
                    filter.setInventorySlotContents(i, r);
                    this.items[i] = null;
                    this.markDirty();
                }
            } else if (f != null) {
                this.items[i] = f;
                filter.setInventorySlotContents(i, null);
                this.markDirty();
            }
        }
        if (!wanted) {
            filter.jam();
            this.delay = MIN_DELAY;
            this.countdown = 0;
            this.inFlight = false;
            return;
        }
        if (this.inFlight) {
            filter.jam();
        }
        if (this.pulseTicks > 0) {
            return;
        }
        if (this.countdown > 0) {
            this.countdown--;
            return;
        }
        this.inFlight = false;
        int extractions = filter.getExtractions();
        int arrivals = this.arrivals;
        this.pulse();
        if (filter.getExtractions() == extractions) {
            // Nothing to fetch: back off, up to 20 seconds between pulses
            this.countdown = this.delay;
            this.delay = Math.min(MAX_DELAY, this.delay * 2);
        } else {
            // A stack still in the tubes must arrive before the next pulse, or it gets fetched twice
            this.delay = MIN_DELAY;
            this.inFlight = this.arrivals == arrivals;
            this.countdown = this.inFlight ? MAX_DELAY : SETTLE;
        }
    }

    /** Matching stock per T square in the output inventory plus the buffer, or null without one. */
    private int[] countStock(ForgeDirection face) {
        IItemHandler handler = MachineLib.getHandler(this.worldObj, this.getPos().offset(face), face.getOpposite());
        if (handler == null) {
            return null;
        }
        int[] have = new int[GRID];
        for (int s = 0; s < handler.getSlots(); s++) {
            this.addStock(have, handler.getStackInSlot(s));
        }
        this.addStock(have, this.items[SLOT_BUFFER]);
        return have;
    }

    private void addStock(int[] have, ItemStack stack) {
        if (Stacks.isEmpty(stack)) {
            return;
        }
        for (int i = 0; i < GRID; i++) {
            if (sameItem(this.targets[i], stack)) {
                have[i] += stack.stackSize;
            }
        }
    }

    public static boolean sameItem(ItemStack a, ItemStack b) {
        if (Stacks.isEmpty(a) || Stacks.isEmpty(b)) {
            return false;
        }
        return a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    private void drainBuffer(ForgeDirection face) {
        ItemStack stack = this.items[SLOT_BUFFER];
        if (Stacks.isEmpty(stack)) {
            return;
        }
        TubeItem ti = new TubeItem(face.ordinal(), stack.copy());
        MachineLib.handleItem(this.worldObj, ti, this.getPos(), face);
        if (Stacks.size(ti.item) != stack.stackSize) {
            this.items[SLOT_BUFFER] = Stacks.orNull(ti.item);
            this.markDirty();
        }
    }

    private void pulse() {
        this.pulseTicks = 2;
        this.setActive(true);
    }

    private void setActive(boolean value) {
        if (!(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) instanceof MachineBlock)) {
            return;
        }
        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int next = value ? meta | 8 : meta & ~8;
        if (next != meta) {
            // flag 3: the Retriever behind must see the redstone change
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, next, 3);
        }
    }

    public ForgeDirection getFacing() {
        if (this.worldObj == null
                || !(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) instanceof MachineBlock)) {
            return ForgeDirection.NORTH;
        }
        ForgeDirection face = MachineBlock.getFacing(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord));
        return face == ForgeDirection.UNKNOWN ? ForgeDirection.NORTH : face;
    }

    public ItemStack getTarget(int slot) {
        return this.targets[slot];
    }

    public int getTargetCount(int slot) {
        return this.targetCounts[slot];
    }

    /** Sets a T square to a single-item copy of {@code stack} wanting {@code count}; 0 clears it. */
    public void setTarget(int slot, ItemStack stack, int count) {
        if (Stacks.isEmpty(stack) || count <= 0) {
            this.targets[slot] = null;
            this.targetCounts[slot] = 0;
        } else {
            ItemStack template = stack.copy();
            template.stackSize = 1;
            this.targets[slot] = template;
            this.targetCounts[slot] = Math.min(count, MAX_TARGET);
        }
        this.markDirty();
    }

    /** The T grid as a container of display stacks whose counts are the targets. */
    public IInventory getTargetView() {
        return this.targetView;
    }

    private boolean canAccept(ItemStack stack) {
        ItemStack buffer = this.items[SLOT_BUFFER];
        return buffer == null || (sameItem(buffer, stack)
                && buffer.stackSize + stack.stackSize <= buffer.getMaxStackSize());
    }

    @Override
    public int getTubeConnectableSides() {
        ForgeDirection face = this.getFacing();
        return (1 << face.ordinal()) | (1 << face.getOpposite().ordinal());
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        if (side != this.getFacing().getOpposite().ordinal() || state == 2 || !this.canAccept(ti.item)) {
            return false;
        }
        ItemStack buffer = this.items[SLOT_BUFFER];
        if (buffer == null) {
            this.items[SLOT_BUFFER] = ti.item.copy();
        } else {
            buffer.stackSize += ti.item.stackSize;
        }
        this.arrivals++;
        if (this.inFlight) {
            this.inFlight = false;
            this.countdown = SETTLE;
        }
        this.markDirty();
        return true;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        return side == this.getFacing().getOpposite().ordinal() && state != 2 && this.canAccept(ti.item);
    }

    @Override
    public int tubeWeight(int side, int state) {
        return 0;
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.items[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack result = Stacks.getAndSplit(this.items, slot, amount);
        this.markDirty();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return Stacks.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
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
        return NO_SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        Stacks.saveAllItems(tag, this.items);
        NBTTagCompound t = new NBTTagCompound();
        Stacks.saveAllItems(t, this.targets);
        tag.setTag("Targets", t);
        tag.setIntArray("TargetCounts", this.targetCounts.clone());
        tag.setInteger("Delay", this.delay);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = new ItemStack[SIZE];
        Stacks.loadAllItems(tag, this.items);
        this.targets = new ItemStack[GRID];
        Stacks.loadAllItems(tag.getCompoundTag("Targets"), this.targets);
        int[] counts = tag.getIntArray("TargetCounts");
        int delay = tag.getInteger("Delay");
        this.targetCounts = new int[GRID];
        for (int i = 0; i < GRID; i++) {
            int count = i < counts.length ? Math.min(counts[i], MAX_TARGET) : 0;
            if (this.targets[i] == null || count <= 0) {
                this.targets[i] = null;
            } else {
                this.targetCounts[i] = count;
            }
        }
        this.delay = Math.max(MIN_DELAY, Math.min(MAX_DELAY, delay));
    }

    @Override
    public void dropContents() {
        if (this.worldObj != null) {
            Stacks.dropAll(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.items);
        }
    }

    private final class TargetView implements InvDefaults {
        @Override
        public int getSizeInventory() {
            return GRID;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            ItemStack template = RetrievulatorBlockEntity.this.targets[slot];
            if (template == null) {
                return null;
            }
            ItemStack shown = template.copy();
            shown.stackSize = RetrievulatorBlockEntity.this.targetCounts[slot];
            return shown;
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            return null;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            RetrievulatorBlockEntity.this.setTarget(slot, stack, Stacks.size(stack));
        }

        @Override
        public int getInventoryStackLimit() {
            return MAX_TARGET;
        }

        @Override
        public void markDirty() {
            RetrievulatorBlockEntity.this.markDirty();
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return RetrievulatorBlockEntity.this.isUseableByPlayer(player);
        }
    }
}
