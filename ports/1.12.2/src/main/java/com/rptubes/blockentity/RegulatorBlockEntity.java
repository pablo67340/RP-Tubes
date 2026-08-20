package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.RegulatorMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
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
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Port of TileRegulator — input filter / buffer / output template (27 slots).
 */
public class RegulatorBlockEntity extends TileEntity implements net.minecraft.util.ITickable, ITubeConnectable, ISidedInventory, InvDefaults {
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
    private final IItemHandler handler = new SidedInvWrapper(this, EnumFacing.DOWN);
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    public RegulatorBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, RegulatorBlockEntity be) {
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
        if (this.world == null || this.world.isRemote) {
            return;
        }
        boolean signal = this.world.isBlockPowered(this.pos);
        if (signal) {
            if (this.powered) {
                return;
            }
            this.powered = true;
            this.handleTransfer();
        } else {
            this.powered = false;
        }
        this.markDirty();
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
            if (!stack.isEmpty() && (match.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(match, stack))) {
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
            if (filter != null && !filter.isEmpty() && (filter.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(filter, stack))) {
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
                this.markDirty();
                return stack.isEmpty();
            }
            if ((slot.isItemEqual(toInsert) && ItemStack.areItemStackTagsEqual(slot, toInsert)) && slot.getCount() < slot.getMaxStackSize()) {
                int move = Math.min(toInsert.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(move);
                stack.shrink(move);
                this.markDirty();
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
                    if (slot.isEmpty() || !(filter.isItemEqual(slot) && ItemStack.areItemStackTagsEqual(filter, slot))) {
                        continue;
                    }
                    int take = Math.min(need, slot.getCount());
                    ItemStack out = slot.splitStack(take);
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
            this.markDirty();
        }
    }

    private void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!MachineLib.handleItem(this.world, ti, this.pos, this.getFacing())) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                return;
            }
        }
    }

    public EnumFacing getFacing() {
        return this.world.getBlockState(this.pos).getValue(MachineBlock.FACING);
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getValue(MachineBlock.ACTIVE) != value) {
                this.world.setBlockState(this.pos, state.withProperty(MachineBlock.ACTIVE, value), 2);
            }
        }
        this.markDirty();
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
        EnumFacing face = this.getFacing();
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
        EnumFacing face = this.getFacing();
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
        EnumFacing face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.regulator");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new RegulatorMenu(inv, this);
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
        this.inputMap = null;
        this.outputMap = null;
        this.markDirty();
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        this.inputMap = null;
        this.outputMap = null;
        return ItemStackHelper.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.inputMap = null;
        this.outputMap = null;
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
        this.inputMap = null;
        this.outputMap = null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        EnumFacing face = this.getFacing();
        if (side == face || side == face.getOpposite()) {
            return new int[0];
        }
        return BUFFER_SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return index >= 9 && index < 18;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index >= 9 && index < 18;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
        tag.setByte("mode", this.mode);
        tag.setByte("col", this.color);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.mode = tag.getByte("mode");
        this.color = tag.getByte("col");
        this.inputMap = null;
        this.outputMap = null;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.handler;
        }
        return super.getCapability(cap, side);
    }

    public void dropContents() {
        this.buffer.onRemove(this);
        if (this.world != null) {
            net.minecraft.inventory.InventoryHelper.dropInventoryItems(this.world, this.pos, this);
        }
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        serverTick(this.world, this.pos, this.world.getBlockState(this.pos), this);
    }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                                 net.minecraft.block.state.IBlockState oldState,
                                 net.minecraft.block.state.IBlockState newState) {
        // Default Forge behavior recreates modded tile entities on ANY state
        // change (e.g. lit/active toggles), silently wiping their contents.
        return oldState.getBlock() != newState.getBlock();
    }
}
