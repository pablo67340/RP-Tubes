package com.rptubes.blockentity;

import com.rptubes.inventory.SidedInvWrapper;

import com.rptubes.inventory.ItemHandlerProvider;

import com.rptubes.inventory.IItemHandler;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.RegulatorMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
 * Port of TileRegulator — input filter / buffer / output template (27 slots).
 */
public class RegulatorBlockEntity extends BlockEntity implements net.minecraft.world.level.block.entity.TickableBlockEntity, ITubeConnectable, WorldlyContainer, MenuProvider, ItemHandlerProvider {
    public static final int SIZE = 27;
    private final TubeBuffer buffer = new TubeBuffer();
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private MachineLib.FilterMap inputMap;
    private MachineLib.FilterMap outputMap;
    /** OG mode: 0 = dump the whole buffer on transfer, 1 = transfer output-template amounts. */
    public byte mode;
    /** Paint color applied to ejected items (OG color). */
    public byte color;
    private boolean powered;
    private boolean active;
    private int tickCooldown;
    private final IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH);
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    public RegulatorBlockEntity() {
        super(ModBlockEntities.REGULATOR);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RegulatorBlockEntity be) {
        if (be.tickCooldown > 0) {
            --be.tickCooldown;
        }
        if (!be.buffer.isEmpty()) {
            be.drainBuffer();
            be.tickCooldown = be.buffer.isEmpty() ? 5 : 10;
            return;
        }
        if (be.active && !be.powered && be.tickCooldown <= 0) {
            be.setActive(false);
        }
        if (be.tickCooldown <= 0) {
            be.tickCooldown = 10;
            if (be.bufferSatisfiesOutput()) {
                be.handleTransfer();
            }
        }
    }

    public void onNeighborChange() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        boolean signal = this.level.hasNeighborSignal(this.worldPosition);
        if (signal) {
            if (this.powered) {
                return;
            }
            this.powered = true;
            this.handleTransfer();
        } else {
            this.powered = false;
        }
        this.setChanged();
    }

    private void regenMaps() {
        ItemStack[] arr = this.items.toArray(new ItemStack[0]);
        this.inputMap = MachineLib.makeFilterMap(arr, 0, 9);
        this.outputMap = MachineLib.makeFilterMap(arr, 18, 9);
    }

    private MachineLib.FilterMap inputMap() {
        if (this.inputMap == null) {
            this.regenMaps();
        }
        return this.inputMap;
    }

    private MachineLib.FilterMap outputMap() {
        if (this.outputMap == null) {
            this.regenMaps();
        }
        return this.outputMap;
    }

    private int countInBuffer(ItemStack match) {
        int total = 0;
        for (int i = 9; i < 18; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty() && (ItemStack.isSame(match, stack) && ItemStack.tagMatches(match, stack))) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private int roomFor(ItemStack stack) {
        MachineLib.FilterMap map = this.inputMap();
        if (map.size() == 0) {
            return stack.getCount();
        }
        if (!map.containsKey(stack)) {
            return 0;
        }
        int allowed = 0;
        for (ItemStack filter : map.getFilter()) {
            if (filter != null && !filter.isEmpty() && (ItemStack.isSame(filter, stack) && ItemStack.tagMatches(filter, stack))) {
                allowed += filter.getCount();
            }
        }
        return Math.max(0, allowed - this.countInBuffer(stack));
    }

    private boolean insertIntoBuffer(ItemStack stack) {
        int room = this.roomFor(stack);
        if (room <= 0) {
            return false;
        }
        ItemStack toInsert = stack.copy();
        toInsert.setCount(Math.min(toInsert.getCount(), room));
        for (int i = 9; i < 18; i++) {
            ItemStack slot = this.items.get(i);
            if (slot.isEmpty()) {
                this.items.set(i, toInsert);
                stack.shrink(toInsert.getCount());
                this.setChanged();
                return stack.isEmpty();
            }
            if ((ItemStack.isSame(slot, toInsert) && ItemStack.tagMatches(slot, toInsert)) && slot.getCount() < slot.getMaxStackSize()) {
                int move = Math.min(toInsert.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(move);
                stack.shrink(move);
                this.setChanged();
                if (stack.isEmpty()) {
                    return true;
                }
                toInsert.setCount(stack.getCount());
            }
        }
        return stack.isEmpty();
    }

    private boolean bufferSatisfiesOutput() {
        MachineLib.FilterMap map = this.outputMap();
        if (map.size() == 0) {
            for (int i = 9; i < 18; i++) {
                if (!this.items.get(i).isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        for (ItemStack filter : map.getFilter()) {
            if (filter == null || filter.isEmpty()) {
                continue;
            }
            if (this.countInBuffer(filter) < filter.getCount()) {
                return false;
            }
        }
        return map.size() > 0;
    }

    private void handleTransfer() {
        if (!this.buffer.isEmpty()) {
            this.drainBuffer();
            return;
        }
        MachineLib.FilterMap map = this.outputMap();
        boolean any = false;
        if (this.mode == 0 || map.size() == 0) {
            // OG mode 0: eject everything in the buffer row
            for (int i = 9; i < 18; i++) {
                ItemStack stack = this.items.get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                this.buffer.addNewColor(stack.copy(), this.color);
                this.items.set(i, ItemStack.EMPTY);
                any = true;
            }
        } else {
            for (ItemStack filter : map.getFilter()) {
                if (filter == null || filter.isEmpty()) {
                    continue;
                }
                int need = filter.getCount();
                for (int i = 9; i < 18 && need > 0; i++) {
                    ItemStack slot = this.items.get(i);
                    if (slot.isEmpty() || !(ItemStack.isSame(filter, slot) && ItemStack.tagMatches(filter, slot))) {
                        continue;
                    }
                    int take = Math.min(need, slot.getCount());
                    ItemStack out = slot.split(take);
                    if (slot.isEmpty()) {
                        this.items.set(i, ItemStack.EMPTY);
                    }
                    this.buffer.addNewColor(out, this.color);
                    need -= take;
                    any = true;
                }
            }
        }
        if (any) {
            this.setActive(true);
            this.drainBuffer();
            this.tickCooldown = 5;
            this.setChanged();
        }
    }

    private void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!MachineLib.handleItem(this.level, ti, this.worldPosition, this.getFacing())) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                return;
            }
        }
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(MachineBlock.FACING);
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.level != null) {
            BlockState state = this.getBlockState();
            if (state.getValue(MachineBlock.ACTIVE) != value) {
                this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), 2);
            }
        }
        this.setChanged();
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
        Direction face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            this.buffer.addBounce(ti);
            this.setActive(true);
            this.tickCooldown = 5;
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            ItemStack stack = ti.item.copy();
            if (this.roomFor(stack) <= 0) {
                return false;
            }
            boolean all = this.insertIntoBuffer(stack);
            if (stack.getCount() < ti.item.getCount()) {
                ti.item = stack;
            }
            this.tickCooldown = 2;
            return all;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        Direction face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            return this.roomFor(ti.item) > 0;
        }
        return false;
    }

    @Override
    public int tubeWeight(int side, int state) {
        Direction face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.rptubes.regulator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RegulatorMenu(id, inv, this);
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
        this.inputMap = null;
        this.outputMap = null;
        this.setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.inputMap = null;
        this.outputMap = null;
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.inputMap = null;
        this.outputMap = null;
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
        this.inputMap = null;
        this.outputMap = null;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        Direction face = this.getFacing();
        if (side == face || side == face.getOpposite()) {
            return new int[0];
        }
        return BUFFER_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index >= 9 && index < 18;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index >= 9 && index < 18;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
        tag.putByte("mode", this.mode);
        tag.putByte("col", this.color);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.mode = tag.getByte("mode");
        this.color = tag.getByte("col");
        this.inputMap = null;
        this.outputMap = null;
    }




    public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[Math.abs(side.ordinal() % this.handlers.length)];
    }

    @Override
    public void setRemoved() {
        this.buffer.onRemove(this);
        if (this.level != null) {
            net.minecraft.world.Containers.dropContents(this.level, this.worldPosition, this);
        }
        super.setRemoved();
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        serverTick(this.level, this.worldPosition, this.getBlockState(), this);
    }
}
