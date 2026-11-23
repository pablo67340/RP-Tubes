package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.RetrievulatorMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Port of Tubestuff's Retrievulator. Sits on a Retriever's output and keeps the
 * inventory on its own output topped up to the counts in the T grid, by moving the
 * matching R grid stacks into the Retriever's filter and pulsing it.
 */
public class RetrievulatorBlockEntity extends BlockEntity implements ITubeConnectable, WorldlyContainer, MenuProvider {
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
    // T grid: single-item templates with separate counts, since stack codecs cap at 99
    private NonNullList<ItemStack> targets = NonNullList.withSize(GRID, ItemStack.EMPTY);
    private int[] targetCounts = new int[GRID];
    private final Container targetView = new TargetView();
    private int delay = MIN_DELAY;
    private int countdown;
    private boolean inFlight;
    private int arrivals;
    private int pulseTicks;

    public RetrievulatorBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.RETRIEVULATOR.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.RETRIEVULATOR, pos, state);
        *///?}
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RetrievulatorBlockEntity be) {
        be.tick();
    }

    private void tick() {
        if (this.pulseTicks > 0 && --this.pulseTicks == 0) {
            this.setActive(false);
        }
        Direction face = this.getFacing();
        this.drainBuffer(face);

        BlockEntity behind = this.level.getBlockEntity(this.worldPosition.relative(face.getOpposite()));
        if (!(behind instanceof FilterBlockEntity filter)) {
            return;
        }
        int[] have = this.countStock(face);
        if (have == null) {
            return;
        }
        // Each square must hold its stack in exactly one place, R grid or filter
        for (int i = 0; i < GRID; i++) {
            if (this.targets.get(i).isEmpty()) {
                continue;
            }
            if (this.items.get(i).isEmpty() == filter.getItem(i).isEmpty()) {
                return;
            }
        }
        boolean wanted = false;
        for (int i = 0; i < GRID; i++) {
            if (this.targets.get(i).isEmpty()) {
                continue;
            }
            ItemStack r = this.items.get(i);
            ItemStack f = filter.getItem(i);
            if (have[i] < this.targetCounts[i]) {
                wanted = true;
                if (!r.isEmpty()) {
                    filter.setItem(i, r);
                    this.items.set(i, ItemStack.EMPTY);
                    this.setChanged();
                }
            } else if (!f.isEmpty()) {
                this.items.set(i, f);
                filter.setItem(i, ItemStack.EMPTY);
                this.setChanged();
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
    private int[] countStock(Direction face) {
        var handler = MachineLib.getHandler(this.level, this.worldPosition.relative(face), face.getOpposite());
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

    private static boolean sameItem(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        //? if <1.20.6 {
        /*return ItemStack.isSameItemSameTags(a, b);
        *///?} else {
        return ItemStack.isSameItemSameComponents(a, b);
        //?}
    }

    private void drainBuffer(Direction face) {
        ItemStack stack = this.items.get(SLOT_BUFFER);
        if (stack.isEmpty()) {
            return;
        }
        TubeItem ti = new TubeItem(face.ordinal(), stack.copy());
        MachineLib.handleItem(this.level, ti, this.worldPosition, face);
        if (ti.item.getCount() != stack.getCount()) {
            this.items.set(SLOT_BUFFER, ti.item.isEmpty() ? ItemStack.EMPTY : ti.item);
            this.setChanged();
        }
    }

    private void pulse() {
        this.pulseTicks = 2;
        this.setActive(true);
    }

    private void setActive(boolean value) {
        BlockState state = this.getBlockState();
        if (state.getValue(MachineBlock.ACTIVE) != value) {
            this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), Block.UPDATE_ALL);
        }
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(MachineBlock.FACING);
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
        this.setChanged();
    }

    /** The T grid as a container of display stacks whose counts are the targets. */
    public Container getTargetView() {
        return this.targetView;
    }

    private boolean canAccept(ItemStack stack) {
        ItemStack buffer = this.items.get(SLOT_BUFFER);
        return buffer.isEmpty() || (sameItem(buffer, stack)
                && buffer.getCount() + stack.getCount() <= buffer.getMaxStackSize());
    }

    @Override
    public int getTubeConnectableSides() {
        Direction face = this.getFacing();
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
        this.setChanged();
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
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.retrievulator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RetrievulatorMenu(id, inv, this);
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        this.setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
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
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return NO_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    //? if <1.20.6 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        CompoundTag t = new CompoundTag();
        ContainerHelper.saveAllItems(t, this.targets);
        tag.put("Targets", t);
    *///?} else if <1.21.11 {
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        CompoundTag t = new CompoundTag();
        ContainerHelper.saveAllItems(t, this.targets, registries);
        tag.put("Targets", t);
    //?} else {
    /*protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        ContainerHelper.saveAllItems(tag.child("Targets"), this.targets);
    *///?}
        tag.putIntArray("TargetCounts", this.targetCounts.clone());
        tag.putInt("Delay", this.delay);
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.targets = NonNullList.withSize(GRID, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag.getCompound("Targets"), this.targets);
        int[] counts = tag.getIntArray("TargetCounts");
        int delay = tag.getInt("Delay");
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.targets = NonNullList.withSize(GRID, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag.getCompound("Targets"), this.targets, registries);
        int[] counts = tag.getIntArray("TargetCounts");
        int delay = tag.getInt("Delay");
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.targets = NonNullList.withSize(GRID, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag.childOrEmpty("Targets"), this.targets);
        int[] counts = tag.getIntArray("TargetCounts").orElse(new int[0]);
        int delay = tag.getIntOr("Delay", MIN_DELAY);
    *///?}
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
    public void setRemoved() {
        //? if <1.21.11 {
        if (this.level != null && Removal.isBroken(this)) {
            net.minecraft.world.Containers.dropContents(this.level, this.worldPosition, this);
        }
        //?}
        super.setRemoved();
    }

    private final class TargetView implements Container {
        @Override
        public int getContainerSize() {
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
        public ItemStack getItem(int slot) {
            ItemStack template = RetrievulatorBlockEntity.this.targets.get(slot);
            if (template.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack shown = template.copy();
            shown.setCount(RetrievulatorBlockEntity.this.targetCounts[slot]);
            return shown;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            RetrievulatorBlockEntity.this.setTarget(slot, stack, stack.getCount());
        }

        @Override
        public int getMaxStackSize() {
            return MAX_TARGET;
        }

        @Override
        public void setChanged() {
            RetrievulatorBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return RetrievulatorBlockEntity.this.stillValid(player);
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < GRID; i++) {
                RetrievulatorBlockEntity.this.setTarget(i, ItemStack.EMPTY, 0);
            }
        }
    }
}
