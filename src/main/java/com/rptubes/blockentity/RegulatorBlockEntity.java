package com.rptubes.blockentity;
//? if fabric && >=1.20.6 && <1.21.1 {

/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerHelper;
import com.rptubes.inventory.InvWrapper;
import com.rptubes.inventory.SidedInvWrapper;
*///?}

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
//? if forge {
/*import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
*///?} else if neoforge && <26.2 {
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
//?} else if <1.20.6 || (>=1.21.11 && <26.1.2) {
/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.SidedInvWrapper;
*///?} else if neoforge {
/*import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
*///?} else if >=1.21.1 {
/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerProvider;
import com.rptubes.inventory.SidedInvWrapper;
*///?}

/**
 * Port of TileRegulator — input filter / buffer / output template (27 slots).
 */
//? if forgelike {
public class RegulatorBlockEntity extends BlockEntity implements ITubeConnectable, WorldlyContainer, MenuProvider {
//?} else if <1.21.1 {
/*public class RegulatorBlockEntity extends BlockEntity implements ITubeConnectable, WorldlyContainer, MenuProvider, com.rptubes.inventory.ItemHandlerProvider {
*///?} else if <1.21.11 {
/*public class RegulatorBlockEntity extends BlockEntity implements ITubeConnectable, WorldlyContainer, MenuProvider, ItemHandlerProvider {
*///?} else if <26.1.2 {
/*public class RegulatorBlockEntity extends BlockEntity implements ITubeConnectable, WorldlyContainer, MenuProvider, com.rptubes.inventory.ItemHandlerProvider{
*///?} else {
/*public class RegulatorBlockEntity extends BlockEntity implements ItemHandlerProvider, ITubeConnectable, WorldlyContainer, MenuProvider {
*///?}
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
    //? if fabric && >=1.21.1 && <1.21.11 {
    /*private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    *///?} else if fabric && >=1.21.1 && <26.1.2 {
    /*private IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    *///?} else if neoforge && >=1.21.11 && <26.1.2 {
    /*private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }
    *///?} else if neoforge && >=26.2 {
    /*private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    public ResourceHandler<ItemResource> getItemHandler(Direction side) {
        return new WorldlyContainerWrapper(this, side);
    }
    *///?} else if fabric && <1.21.1 {
    /*private final IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    *///?} else if neoforge && <1.21.11 {
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};


    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }
    //?} else if !forge {
    /*private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }
    *///?} else {
    /*private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    *///?}

    public RegulatorBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.REGULATOR.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.REGULATOR, pos, state);
        *///?}
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
        //? if <1.21.11 {
        if (this.level == null || this.level.isClientSide) {
        //?} else {
        /*if (this.level == null || this.level.isClientSide()) {
        *///?}
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
        ItemStack[] arr = this.items.toArray(ItemStack[]::new);
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
            //? if <1.20.6 {
            /*if (!stack.isEmpty() && ItemStack.isSameItemSameTags(match, stack)) {
            *///?} else {
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(match, stack)) {
            //?}
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
            //? if <1.20.6 {
            /*if (filter != null && !filter.isEmpty() && ItemStack.isSameItemSameTags(filter, stack)) {
            *///?} else {
            if (filter != null && !filter.isEmpty() && ItemStack.isSameItemSameComponents(filter, stack)) {
            //?}
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
            //? if <1.20.6 {
            /*if (ItemStack.isSameItemSameTags(slot, toInsert) && slot.getCount() < slot.getMaxStackSize()) {
            *///?} else {
            if (ItemStack.isSameItemSameComponents(slot, toInsert) && slot.getCount() < slot.getMaxStackSize()) {
            //?}
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
                    //? if <1.20.6 {
                    /*if (slot.isEmpty() || !ItemStack.isSameItemSameTags(filter, slot)) {
                    *///?} else {
                    if (slot.isEmpty() || !ItemStack.isSameItemSameComponents(filter, slot)) {
                    //?}
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
                this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), Block.UPDATE_CLIENTS);
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
        return Component.translatable("block.rptubes.regulator");
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
    //? if <1.20.6 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag);
    *///?} else if <1.21.11 {
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        this.buffer.writeToNBT(tag, registries);
    //?} else {
    /*protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag, this.level != null ? this.level.registryAccess() : null);
    *///?}
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
        tag.putByte("mode", this.mode);
        tag.putByte("col", this.color);
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.mode = tag.getByte("mode");
        this.color = tag.getByte("col");
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.buffer.readFromNBT(tag, registries);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.mode = tag.getByte("mode");
        this.color = tag.getByte("col");
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBooleanOr("Powered", false);
        this.active = tag.getBooleanOr("Active", false);
        this.mode = tag.getByteOr("mode", (byte) 0);
        this.color = tag.getByteOr("col", (byte) 0);
    *///?}
        this.inputMap = null;
        this.outputMap = null;
    }

    //? if forge {
    /*@Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER && side != null) {
            return handlers[side.ordinal()].cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<? extends IItemHandler> h : handlers) {
            h.invalidate();
        }
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this, Direction.values());
    }

    @Override
    *///?} else if <1.20.6 {
    /*public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }

    @Override
    *///?} else if fabric && <1.21.1 {

    /*public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }

    @Override
    *///?} else if fabric && <1.21.11 {



    /*@Override
    *///?} else if fabric || <1.21.11 || (>=26.1.2 && <26.2) {

    @Override
    //?} else {
    /*@Override
    *///?}
    public void setRemoved() {
        this.buffer.onRemove(this);
        //? if <1.21.11 {
        if (this.level != null && Removal.isBroken(this)) {
            net.minecraft.world.Containers.dropContents(this.level, this.worldPosition, this);
        }
        super.setRemoved();
        //?} else {
        /*// Item drops handled by the default preRemoveSideEffects (Container)
        super.setRemoved();
        *///?}
    }

    //? if >=1.21.11 {
    /*@Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        Removal.markBroken(this);
        super.preRemoveSideEffects(pos, state);
    }
    *///?}
    //? if fabric && >=1.21.11 && <26.1.2 {

    /*@Override
    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }
    *///?}
}
