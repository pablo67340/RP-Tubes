package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.RegulatorMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import com.rptubes.util.inv.IItemHandler;
import com.rptubes.util.inv.SidedInvWrapper;

/**
 * Port of TileRegulator — input filter / buffer / output template (27 slots).
 */
public class RegulatorBlockEntity extends CompatTileEntity implements ITubeConnectable, ISidedInventory, InvDefaults, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    public static final int SIZE = 27;
    private final TubeBuffer buffer = new TubeBuffer();
    private ItemStack[] items = new ItemStack[SIZE];
    private MachineLib.FilterMap inputMap;
    private MachineLib.FilterMap outputMap;
    /** OG mode: 0 = dump the whole buffer on transfer, 1 = transfer output-template amounts. */
    public byte mode;
    /** Paint color applied to ejected items (OG color). */
    public byte color;
    private boolean powered;
    private boolean active;
    private int tickCooldown;
    private final IItemHandler handler = new SidedInvWrapper(this, ForgeDirection.DOWN);
    private static final int[] BUFFER_SLOTS = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    public RegulatorBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, RegulatorBlockEntity be) {
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
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        boolean signal = this.getWorld().isBlockIndirectlyGettingPowered(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
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
        ItemStack[] arr = this.items;
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
            ItemStack stack = this.items[i];
            if (!com.rptubes.util.Stacks.isEmpty(stack) && (match.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(match, stack))) {
                total += stack.stackSize;
            }
        }
        return total;
    }

    private int roomFor(ItemStack stack) {
        MachineLib.FilterMap map = this.inputMap();
        if (map.size() == 0) {
            return stack.stackSize;
        }
        if (!map.containsKey(stack)) {
            return 0;
        }
        int allowed = 0;
        for (ItemStack filter : map.getFilter()) {
            if (filter != null && !com.rptubes.util.Stacks.isEmpty(filter) && (filter.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(filter, stack))) {
                allowed += filter.stackSize;
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
        toInsert.stackSize = Math.min(toInsert.stackSize, room);
        for (int i = 9; i < 18; i++) {
            ItemStack slot = this.items[i];
            if (com.rptubes.util.Stacks.isEmpty(slot)) {
                this.items[i] = toInsert;
                stack.stackSize -= toInsert.stackSize;
                this.markDirty();
                return com.rptubes.util.Stacks.isEmpty(stack);
            }
            if ((slot.isItemEqual(toInsert) && ItemStack.areItemStackTagsEqual(slot, toInsert)) && slot.stackSize < slot.getMaxStackSize()) {
                int move = Math.min(toInsert.stackSize, slot.getMaxStackSize() - slot.stackSize);
                slot.stackSize += move;
                stack.stackSize -= move;
                this.markDirty();
                if (com.rptubes.util.Stacks.isEmpty(stack)) {
                    return true;
                }
                toInsert.stackSize = stack.stackSize;
            }
        }
        return com.rptubes.util.Stacks.isEmpty(stack);
    }

    private boolean bufferSatisfiesOutput() {
        MachineLib.FilterMap map = this.outputMap();
        if (map.size() == 0) {
            for (int i = 9; i < 18; i++) {
                if (!com.rptubes.util.Stacks.isEmpty(this.items[i])) {
                    return true;
                }
            }
            return false;
        }
        for (ItemStack filter : map.getFilter()) {
            if (filter == null || com.rptubes.util.Stacks.isEmpty(filter)) {
                continue;
            }
            if (this.countInBuffer(filter) < filter.stackSize) {
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
                ItemStack stack = this.items[i];
                if (com.rptubes.util.Stacks.isEmpty(stack)) {
                    continue;
                }
                this.buffer.addNewColor(stack.copy(), this.color);
                this.items[i] = null;
                any = true;
            }
        } else {
            for (ItemStack filter : map.getFilter()) {
                if (filter == null || com.rptubes.util.Stacks.isEmpty(filter)) {
                    continue;
                }
                int need = filter.stackSize;
                for (int i = 9; i < 18 && need > 0; i++) {
                    ItemStack slot = this.items[i];
                    if (com.rptubes.util.Stacks.isEmpty(slot) || !(filter.isItemEqual(slot) && ItemStack.areItemStackTagsEqual(filter, slot))) {
                        continue;
                    }
                    int take = Math.min(need, slot.stackSize);
                    ItemStack out = slot.splitStack(take);
                    if (com.rptubes.util.Stacks.isEmpty(slot)) {
                        this.items[i] = null;
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
            if (!MachineLib.handleItem(this.getWorld(), ti, this.getPos(), this.getFacing())) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                return;
            }
        }
    }

    public ForgeDirection getFacing() {
        return MachineBlock.getFacing(this.getWorld().getBlockMetadata(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.getWorld() != null) {
            MachineBlock.setActive(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, value);
        }
        this.markDirty();
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
        ForgeDirection face = this.getFacing();
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
            if (stack.stackSize < ti.item.stackSize) {
                ti.item = stack;
            }
            this.tickCooldown = 2;
            return all;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        ForgeDirection face = this.getFacing();
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
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.regulator");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new RegulatorMenu(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

        public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.items[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack result = com.rptubes.util.Stacks.getAndSplit(this.items, slot, amount);
        this.inputMap = null;
        this.outputMap = null;
        this.markDirty();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        this.inputMap = null;
        this.outputMap = null;
        return com.rptubes.util.Stacks.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
        this.inputMap = null;
        this.outputMap = null;
        this.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getWorld() != null
                && this.getWorld().getTileEntity(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) == this
                && player.getDistanceSq(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5,
                this.getPos().getZ() + 0.5) <= 64.0;
    }

        public void clear() {
        java.util.Arrays.fill(this.items, null);
        this.inputMap = null;
        this.outputMap = null;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() || side == face.getOpposite().ordinal()) {
            return new int[0];
        }
        return BUFFER_SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return index >= 9 && index < 18;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return index >= 9 && index < 18;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
        tag.setByte("mode", this.mode);
        tag.setByte("col", this.color);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = new ItemStack[SIZE];
        com.rptubes.util.Stacks.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.mode = tag.getByte("mode");
        this.color = tag.getByte("col");
        this.inputMap = null;
        this.outputMap = null;
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        return this.handler;
    }

    public void dropContents() {
        this.buffer.onRemove(this);
        if (this.getWorld() != null) {
            com.rptubes.util.Stacks.dropInventory(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    @Override
    public void updateEntity() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        serverTick(this.getWorld(), this.getPos(), this);
    }
}
