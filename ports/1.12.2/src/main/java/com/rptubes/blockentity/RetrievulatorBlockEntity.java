package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

/**
 * Port of Tubestuff's Retrievulator. Sits on a Retriever's output and keeps the
 * inventory on its own output topped up to the counts in the T grid, by moving the
 * matching R grid stacks into the Retriever's filter and pulsing it.
 */
public class RetrievulatorBlockEntity extends TileEntity implements DropsContents, ITickable, ITubeConnectable, ISidedInventory, InvDefaults {
    public static final int GRID = 9;
    public static final int SLOT_BUFFER = 9;
    public static final int SIZE = 10;
    public static final int MAX_TARGET = 127;
    private static final int MIN_DELAY = 10;
    private static final int MAX_DELAY = 400;
    private static final int SETTLE = 4;
    private static final int[] NO_SLOTS = new int[0];

    // R grid (0-8) and the incoming buffer (9); real items
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    // T grid: single-item templates with separate counts, matching the modern versions
    private NonNullList<ItemStack> targets = NonNullList.withSize(GRID, ItemStack.EMPTY);
    private int[] targetCounts = new int[GRID];
    private final IInventory targetView = new TargetView();
    private int delay = MIN_DELAY;
    private int countdown;
    private boolean inFlight;
    private int arrivals;
    private int pulseTicks;

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        if (this.pulseTicks > 0 && --this.pulseTicks == 0) {
            this.setActive(false);
        }
        EnumFacing face = this.getFacing();
        this.drainBuffer(face);

        TileEntity behind = this.world.getTileEntity(this.pos.offset(face.getOpposite()));
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
            if (this.targets.get(i).isEmpty()) {
                continue;
            }
            if (this.items.get(i).isEmpty() == filter.getStackInSlot(i).isEmpty()) {
                return;
            }
        }
        boolean wanted = false;
        for (int i = 0; i < GRID; i++) {
            if (this.targets.get(i).isEmpty()) {
                continue;
            }
            ItemStack r = this.items.get(i);
            ItemStack f = filter.getStackInSlot(i);
            if (have[i] < this.targetCounts[i]) {
                wanted = true;
                if (!r.isEmpty()) {
                    filter.setInventorySlotContents(i, r);
                    this.items.set(i, ItemStack.EMPTY);
                    this.markDirty();
                }
            } else if (!f.isEmpty()) {
                this.items.set(i, f);
                filter.setInventorySlotContents(i, ItemStack.EMPTY);
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
    private int[] countStock(EnumFacing face) {
        IItemHandler handler = MachineLib.getHandler(this.world, this.pos.offset(face), face.getOpposite());
        if (handler == null) {
            return null;
        }
        int[] have = new int[GRID];
        for (int s = 0; s < handler.getSlots(); s++) {
            this.addStock(have, handler.getStackInSlot(s));
        }
        this.addStock(have, this.items.get(SLOT_BUFFER));
        return have;
    }

    private void addStock(int[] have, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        for (int i = 0; i < GRID; i++) {
            if (sameItem(this.targets.get(i), stack)) {
                have[i] += stack.getCount();
            }
        }
    }

    public static boolean sameItem(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        return ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    private void drainBuffer(EnumFacing face) {
        ItemStack stack = this.items.get(SLOT_BUFFER);
        if (stack.isEmpty()) {
            return;
        }
        TubeItem ti = new TubeItem(face.ordinal(), stack.copy());
        MachineLib.handleItem(this.world, ti, this.pos, face);
        if (ti.item.getCount() != stack.getCount()) {
            this.items.set(SLOT_BUFFER, ti.item.isEmpty() ? ItemStack.EMPTY : ti.item);
            this.markDirty();
        }
    }

    private void pulse() {
        this.pulseTicks = 2;
        this.setActive(true);
    }

    private void setActive(boolean value) {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() instanceof MachineBlock && state.getValue(MachineBlock.ACTIVE) != value) {
            this.world.setBlockState(this.pos, state.withProperty(MachineBlock.ACTIVE, value), 3);
        }
    }

    public EnumFacing getFacing() {
        if (this.world == null) {
            return EnumFacing.NORTH;
        }
        IBlockState state = this.world.getBlockState(this.pos);
        return state.getBlock() instanceof MachineBlock ? state.getValue(MachineBlock.FACING) : EnumFacing.NORTH;
    }

    public ItemStack getTarget(int slot) {
        return this.targets.get(slot);
    }

    public int getTargetCount(int slot) {
        return this.targetCounts[slot];
    }

    /** Sets a T square to a single-item copy of {@code stack} wanting {@code count}; 0 clears it. */
    public void setTarget(int slot, ItemStack stack, int count) {
        if (stack.isEmpty() || count <= 0) {
            this.targets.set(slot, ItemStack.EMPTY);
            this.targetCounts[slot] = 0;
        } else {
            ItemStack template = stack.copy();
            template.setCount(1);
            this.targets.set(slot, template);
            this.targetCounts[slot] = Math.min(count, MAX_TARGET);
        }
        this.markDirty();
    }

    /** The T grid as a container of display stacks whose counts are the targets. */
    public IInventory getTargetView() {
        return this.targetView;
    }

    private boolean canAccept(ItemStack stack) {
        ItemStack buffer = this.items.get(SLOT_BUFFER);
        return buffer.isEmpty() || (sameItem(buffer, stack)
                && buffer.getCount() + stack.getCount() <= buffer.getMaxStackSize());
    }

    @Override
    public int getTubeConnectableSides() {
        EnumFacing face = this.getFacing();
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
        ItemStack buffer = this.items.get(SLOT_BUFFER);
        if (buffer.isEmpty()) {
            this.items.set(SLOT_BUFFER, ti.item.copy());
        } else {
            buffer.grow(ti.item.getCount());
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
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.retrievulator");
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
        ItemStack result = ItemStackHelper.getAndSplit(this.items, slot, amount);
        this.markDirty();
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return ItemStackHelper.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items.set(slot, stack);
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
        return NO_SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        NBTTagCompound t = new NBTTagCompound();
        ItemStackHelper.saveAllItems(t, this.targets);
        tag.setTag("Targets", t);
        tag.setIntArray("TargetCounts", this.targetCounts.clone());
        tag.setInteger("Delay", this.delay);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.targets = NonNullList.withSize(GRID, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag.getCompoundTag("Targets"), this.targets);
        int[] counts = tag.getIntArray("TargetCounts");
        int delay = tag.getInteger("Delay");
        this.targetCounts = new int[GRID];
        for (int i = 0; i < GRID; i++) {
            int count = i < counts.length ? Math.min(counts[i], MAX_TARGET) : 0;
            if (this.targets.get(i).isEmpty() || count <= 0) {
                this.targets.set(i, ItemStack.EMPTY);
            } else {
                this.targetCounts[i] = count;
            }
        }
        this.delay = Math.max(MIN_DELAY, Math.min(MAX_DELAY, delay));
    }

    @Override
    public void dropContents() {
        if (this.world != null) {
            InventoryHelper.dropInventoryItems(this.world, this.pos, this);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    private final class TargetView implements InvDefaults {
        @Override
        public int getSizeInventory() {
            return GRID;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : RetrievulatorBlockEntity.this.targets) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            ItemStack template = RetrievulatorBlockEntity.this.targets.get(slot);
            if (template.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack shown = template.copy();
            shown.setCount(RetrievulatorBlockEntity.this.targetCounts[slot]);
            return shown;
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStackFromSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            RetrievulatorBlockEntity.this.setTarget(slot, stack, stack.getCount());
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
        public boolean isUsableByPlayer(EntityPlayer player) {
            return RetrievulatorBlockEntity.this.isUsableByPlayer(player);
        }

        @Override
        public void clear() {
            for (int i = 0; i < GRID; i++) {
                RetrievulatorBlockEntity.this.setTarget(i, ItemStack.EMPTY, 0);
            }
        }

        @Override
        public ITextComponent getDisplayName() {
            return RetrievulatorBlockEntity.this.getDisplayName();
        }
    }
}
