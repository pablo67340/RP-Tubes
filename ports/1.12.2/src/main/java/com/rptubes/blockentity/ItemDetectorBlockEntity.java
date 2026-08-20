package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.ItemDetectorMenu;
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

/** Port of TileItemDetect — pass-through tube machine that emits redstone when items pass. */
public class ItemDetectorBlockEntity extends TileEntity implements net.minecraft.util.ITickable, ITubeConnectable, ISidedInventory, InvDefaults {
    private final TubeBuffer buffer = new TubeBuffer();
    private NonNullList<ItemStack> filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
    private MachineLib.FilterMap filterMap;
    private boolean active;
    private boolean emitting;
    /** OG mode: 0 = pulse per item, 1 = pulse per stack, 2 = powered while plugged. */
    public byte mode;
    /** Pending pulses (OG count). */
    private int count;
    /** OG Delay — spaces the pulse train into 2 ticks on / 2 ticks off. */
    private boolean delay;

    public ItemDetectorBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, ItemDetectorBlockEntity be) {
        if (!be.buffer.isEmpty()) {
            be.drainBuffer();
        }
        // OG n_(): emit one 2-tick pulse per counted unit, 2-tick gap between pulses
        if (be.mode != 2) {
            if (be.emitting) {
                if (be.delay) {
                    be.delay = false;
                } else {
                    if (be.count > 0) {
                        be.delay = true;
                    }
                    be.setEmitting(false);
                }
            } else if (be.count > 0) {
                if (be.delay) {
                    be.delay = false;
                } else {
                    --be.count;
                    be.delay = true;
                    be.setEmitting(true);
                }
            }
        }
        if (be.buffer.isEmpty() && be.active && !be.emitting && be.count == 0) {
            be.setActive(false);
        }
    }

    private void regenFilterMap() {
        this.filterMap = MachineLib.makeFilterMap(this.filterSlots.toArray(new ItemStack[0]));
    }

    private MachineLib.FilterMap map() {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        return this.filterMap;
    }

    private boolean matchesFilter(ItemStack stack) {
        MachineLib.FilterMap map = this.map();
        return map.size() == 0 || map.containsKey(stack);
    }

    /** OG counting: mode 0 adds the stack size, mode 1 adds one pulse per stack. */
    private void onItemPassed(ItemStack stack) {
        if (this.matchesFilter(stack)) {
            if (this.mode == 0) {
                this.count += stack.getCount();
            } else if (this.mode == 1) {
                ++this.count;
            }
            this.markDirty();
        }
    }

    public boolean isEmitting() {
        return this.emitting;
    }

    private void setEmitting(boolean value) {
        if (this.emitting == value) {
            return;
        }
        this.emitting = value;
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyNeighborsOfStateChange(this.pos, state.getBlock(), false);
            for (EnumFacing dir : EnumFacing.values()) {
                this.world.notifyNeighborsOfStateChange(this.pos.offset(dir), state.getBlock(), false);
            }
        }
        this.markDirty();
    }

    private void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!MachineLib.handleItem(this.world, ti, this.pos, this.getFacing())) {
                this.buffer.plugged = true;
                this.onPlugged();
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                this.onPlugged();
                return;
            }
        }
        // OG mode 2: signal follows the plugged state — clear once the buffer empties
        if (this.mode == 2 && this.emitting) {
            this.setEmitting(false);
        }
    }

    /** OG mode 2: latch the signal on while an item is stuck inside. */
    private void onPlugged() {
        if (this.mode == 2 && !this.emitting) {
            this.delay = false;
            this.count = 0;
            this.setEmitting(true);
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
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            if (!this.buffer.isEmpty()) {
                return false;
            }
            this.onItemPassed(ti.item);
            this.buffer.add(ti);
            this.setActive(true);
            this.drainBuffer();
            return true;
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
            return this.buffer.isEmpty();
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
        return new TextComponentTranslation("block.rptubes.item_detector");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new ItemDetectorMenu(inv, this);
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.filterSlots);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Active", this.active);
        tag.setBoolean("Emitting", this.emitting);
        tag.setByte("mode", this.mode);
        tag.setInteger("cnt", this.count);
        tag.setBoolean("Delay", this.delay);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.filterSlots);
        this.buffer.readFromNBT(tag);
        this.active = tag.getBoolean("Active");
        this.emitting = tag.getBoolean("Emitting");
        this.mode = tag.getByte("mode");
        this.count = tag.getInteger("cnt");
        this.delay = tag.getBoolean("Delay");
        this.filterMap = null;
    }

    public void dropContents() {
        this.buffer.onRemove(this);
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty() && this.world != null) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(this.world,
                        this.pos.getX(), this.pos.getY(), this.pos.getZ(), stack);
            }
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
