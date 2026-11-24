package com.rptubes.blockentity;
//? if fabric && >=1.20.6 && <1.21.1 {

/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerHelper;
import com.rptubes.inventory.InvWrapper;
import com.rptubes.inventory.SidedInvWrapper;
*///?}

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.EjectorMenu;
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

/** Port of TileEject — redstone pulse ejects one item from internal buffer into a tube. */
//? if forgelike {
public class EjectorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, ITubeConnectable {
//?} else if <1.20.6 {
/*public class EjectorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, ITubeConnectable, com.rptubes.inventory.ItemHandlerProvider {
*///?} else if <1.21.1 {
/*public class EjectorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, com.rptubes.inventory.ItemHandlerProvider, ITubeConnectable {
*///?} else if <1.21.11 {
/*public class EjectorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, ITubeConnectable, ItemHandlerProvider {
*///?} else if <26.1.2 {
/*public class EjectorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, ITubeConnectable, com.rptubes.inventory.ItemHandlerProvider{
*///?} else {
/*public class EjectorBlockEntity extends BlockEntity implements ItemHandlerProvider, WorldlyContainer, MenuProvider, ITubeConnectable {
*///?}
    protected final TubeBuffer buffer = new TubeBuffer();
    protected NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    protected boolean powered;
    protected boolean active;
    protected int tickCooldown;
    //? if forge {
    /*private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    *///?} else if neoforge && <1.21.11 {


    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }
    //?} else if <1.21.1 {
    /*private final IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    *///?} else if fabric && >=1.21.11 && <26.1.2 {
    /*private IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    *///?} else if neoforge && >=26.2 {
    /*public ResourceHandler<ItemResource> getItemHandler(Direction side) {
        return new WorldlyContainerWrapper(this, side);
    }
    *///?} else {
    /*private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }
    *///?}

    public EjectorBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.EJECTOR.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.EJECTOR, pos, state);
        *///?}
    }

    protected EjectorBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
                                 BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EjectorBlockEntity be) {
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
        if (com.rptubes.tube.TubeAccess.connectable(this.level, src) != null) {
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
                this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), Block.UPDATE_CLIENTS);
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
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.ejector");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
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
        return ContainerHelper.removeItem(this.items, slot, amount);
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
    }

    @Override
    //? if fabric && <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }

    *///?} else if fabric && <1.21.1 {
    /*protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.buffer.readFromNBT(tag, registries);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }


    public IItemHandler getItemHandler(Direction side) {
        return this.handlers[side == null ? 0 : side.ordinal()];
    }

    *///?} else if fabric && <1.21.11 {
    /*protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.buffer.readFromNBT(tag, registries);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }




    *///?} else if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    @Override
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

    *///?} else if forge && <1.21.11 {
    /*protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.buffer.readFromNBT(tag, registries);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    @Override
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

    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.buffer.readFromNBT(tag, registries);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }


    //?} else if (neoforge && <26.1.2) || (neoforge && >=26.2) {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBooleanOr("Powered", false);
        this.active = tag.getBooleanOr("Active", false);
    }

    *///?} else if forge {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBooleanOr("Powered", false);
        this.active = tag.getBooleanOr("Active", false);
    }

    @Override
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

    *///?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBooleanOr("Powered", false);
        this.active = tag.getBooleanOr("Active", false);
    }


    *///?}
    @Override
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
