package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.EjectorMenu;
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

/** Port of TileEject — redstone pulse ejects one item from internal buffer into a tube. */
public class EjectorBlockEntity extends CompatTileEntity implements ISidedInventory, InvDefaults, ITubeConnectable, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    protected final TubeBuffer buffer = new TubeBuffer();
    protected ItemStack[] items = new ItemStack[9];
    protected boolean powered;
    protected boolean active;
    protected int tickCooldown;
    private final IItemHandler handler = new SidedInvWrapper(this, ForgeDirection.DOWN);

    public EjectorBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, EjectorBlockEntity be) {
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
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        boolean signal = this.getWorld().isBlockIndirectlyGettingPowered(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
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
            ItemStack stack = this.items[i];
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                continue;
            }
            ItemStack one = stack.splitStack(1);
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                this.items[i] = null;
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
        if (this.getWorld() == null) {
            return false;
        }
        BlockPos src = this.getPos().offset(this.getFacing().getOpposite());
        if (this.getWorld().getTileEntity(src.getX(), src.getY(), src.getZ()) instanceof ITubeConnectable) {
            return false;
        }
        ItemStack extracted = MachineLib.extractOne(this.getWorld(), src, this.getFacing());
        if (com.rptubes.util.Stacks.isEmpty(extracted)) {
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

    protected void setActive(boolean value) {
        this.active = value;
        if (this.getWorld() != null) {
            MachineBlock.setActive(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, value);
        }
        this.markDirty();
    }

    @Override
    public int getTubeConnectableSides() {
        // Front ejects; back is the input side (tube deliveries route into the
        // 9-slot buffer through the item handler exposed there).
        ForgeDirection facing = this.getFacing();
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

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.ejector");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new EjectorMenu(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return 9;
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
        return com.rptubes.util.Stacks.getAndSplit(this.items, slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return com.rptubes.util.Stacks.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
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
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == this.getFacing().ordinal()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return side != this.getFacing().ordinal();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return side != this.getFacing().ordinal();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = new ItemStack[9];
        com.rptubes.util.Stacks.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        if (side == null || side == ForgeDirection.UNKNOWN) {
            return this.handler;
        }
        return new SidedInvWrapper(this, side);
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
