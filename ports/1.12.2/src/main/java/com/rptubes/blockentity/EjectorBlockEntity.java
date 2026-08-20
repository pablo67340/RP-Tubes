package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.EjectorMenu;
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

/** Port of TileEject — redstone pulse ejects one item from internal buffer into a tube. */
public class EjectorBlockEntity extends TileEntity implements net.minecraft.util.ITickable, ISidedInventory, InvDefaults, ITubeConnectable {
    protected final TubeBuffer buffer = new TubeBuffer();
    protected NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    protected boolean powered;
    protected boolean active;
    protected int tickCooldown;
    private final IItemHandler handler = new SidedInvWrapper(this, EnumFacing.DOWN);
    private final IItemHandler[] sideHandlers = new IItemHandler[]{
            new SidedInvWrapper(this, EnumFacing.DOWN), new SidedInvWrapper(this, EnumFacing.UP),
            new SidedInvWrapper(this, EnumFacing.NORTH), new SidedInvWrapper(this, EnumFacing.SOUTH),
            new SidedInvWrapper(this, EnumFacing.WEST), new SidedInvWrapper(this, EnumFacing.EAST)};

    public EjectorBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, EjectorBlockEntity be) {
        if (be.tickCooldown > 0) {
            --be.tickCooldown;
        }
        if (!be.buffer.isEmpty()) {
            be.drainBuffer();
            be.tickCooldown = be.buffer.isEmpty() ? 5 : 10;
            return;
        }
        if (!be.powered && be.active) {
            be.setActive(false);
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
            if (!this.active) {
                this.setActive(true);
                this.handleExtract();
            }
        } else {
            if (this.active && this.tickCooldown <= 0) {
                this.tickCooldown = 5;
            }
            this.powered = false;
        }
        this.markDirty();
    }

    protected boolean handleExtract() {
        if (this.extractInternal()) {
            return true;
        }
        return this.extractFromBehind();
    }

    /** Eject one item from the internal 9-slot buffer. */
    protected boolean extractInternal() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack one = stack.splitStack(1);
            if (stack.isEmpty()) {
                this.items.set(i, ItemStack.EMPTY);
            }
            this.buffer.addNew(one);
            this.drainBuffer();
            this.markDirty();
            return true;
        }
        return false;
    }

    /** When the internal buffer is empty, pull one item from the inventory behind (input side). */
    protected boolean extractFromBehind() {
        if (this.world == null) {
            return false;
        }
        BlockPos src = this.pos.offset(this.getFacing().getOpposite());
        if (this.world.getTileEntity(src) instanceof ITubeConnectable) {
            return false;
        }
        ItemStack extracted = MachineLib.extractOne(this.world, src, this.getFacing());
        if (extracted.isEmpty()) {
            return false;
        }
        this.buffer.addNew(extracted);
        this.drainBuffer();
        this.markDirty();
        return true;
    }

    protected void drainBuffer() {
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

    protected void setActive(boolean value) {
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
        // Front ejects; back is the input side (tube deliveries route into the
        // 9-slot buffer through the item handler exposed there).
        EnumFacing facing = this.getFacing();
        return (1 << facing.ordinal()) | (1 << facing.getOpposite().ordinal());
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
        if (side == this.getFacing().ordinal() && state == 2) {
            this.buffer.addBounce(ti);
            this.setActive(true);
            this.tickCooldown = 5;
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        return side == this.getFacing().ordinal() && state == 2;
    }

    @Override
    public int tubeWeight(int side, int state) {
        if (side == this.getFacing().ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.ejector");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new EjectorMenu(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return 9;
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
        return ItemStackHelper.getAndSplit(this.items, slot, amount);
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
        if (side == this.getFacing()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != this.getFacing();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != this.getFacing();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) (side == null ? this.handler : this.sideHandlers[side.getIndex()]);
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
