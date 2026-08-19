package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.EjectorMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/** Port of TileEject — redstone pulse ejects one item from internal buffer into a tube. */
public class EjectorBlockEntity extends TileEntity implements net.minecraft.tileentity.ITickableTileEntity, ISidedInventory, INamedContainerProvider, ITubeConnectable {
    protected final TubeBuffer buffer = new TubeBuffer();
    protected NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    protected boolean powered;
    protected boolean active;
    protected int tickCooldown;
    private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.values());

    public EjectorBlockEntity() {
        super(ModBlockEntities.EJECTOR.get());
    }

    protected EjectorBlockEntity(net.minecraft.tileentity.TileEntityType<?> type) {
        super(type);
    }

    public static void serverTick(World level, BlockPos pos, BlockState state, EjectorBlockEntity be) {
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
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        boolean signal = this.level.hasNeighborSignal(this.worldPosition);
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
        this.setChanged();
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
            ItemStack one = stack.split(1);
            if (stack.isEmpty()) {
                this.items.set(i, ItemStack.EMPTY);
            }
            this.buffer.addNew(one);
            this.drainBuffer();
            this.setChanged();
            return true;
        }
        return false;
    }

    /** When the internal buffer is empty, pull one item from the inventory behind (input side). */
    protected boolean extractFromBehind() {
        if (this.level == null) {
            return false;
        }
        BlockPos src = this.worldPosition.relative(this.getFacing().getOpposite());
        if (this.level.getBlockEntity(src) instanceof ITubeConnectable) {
            return false;
        }
        ItemStack extracted = MachineLib.extractOne(this.level, src, this.getFacing());
        if (extracted.isEmpty()) {
            return false;
        }
        this.buffer.addNew(extracted);
        this.drainBuffer();
        this.setChanged();
        return true;
    }

    protected void drainBuffer() {
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

    protected void setActive(boolean value) {
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
        // Front ejects; back is the input side (tube deliveries route into the
        // 9-slot buffer through the item handler exposed there).
        Direction facing = this.getFacing();
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
        return new TranslationTextComponent("block.rptubes.ejector");
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new EjectorMenu(id, inv, this);
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStackHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStackHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
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
        if (side == this.getFacing()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction != this.getFacing();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction != this.getFacing();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        this.buffer.writeToNBT(tag);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null) {
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
    public void setRemoved() {
        this.buffer.onRemove(this);
        if (this.level != null) {
            net.minecraft.inventory.InventoryHelper.dropContents(this.level, this.worldPosition, this);
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
