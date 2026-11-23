package com.rptubes.blockentity;
//? if fabric && >=1.20.6 && <1.21.1 {

/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerHelper;
import com.rptubes.inventory.InvWrapper;
import com.rptubes.inventory.SidedInvWrapper;
*///?}

import com.rptubes.menu.FilterMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.MachineLib;
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
import net.minecraft.world.level.block.state.BlockState;
//? if neoforge && >=26.2 {
/*import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
*///?} else if (fabric && <1.20.6) || (fabric && >=1.21.11 && <26.1.2) {
/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.SidedInvWrapper;
*///?} else if neoforge && <1.21.11 {
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
//?} else if neoforge {
/*import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
*///?} else if fabric && >=1.21.1 {
/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerProvider;
import com.rptubes.inventory.SidedInvWrapper;
*///?} else if forge {
/*import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
*///?}

/**
 * Port of TileFilter — stack extract + optional filter slots + paint color.
 */
//? if forgelike {
public class FilterBlockEntity extends TransposerBlockEntity implements WorldlyContainer, MenuProvider {
//?} else if <1.21.1 {
/*public class FilterBlockEntity extends TransposerBlockEntity implements WorldlyContainer, MenuProvider, com.rptubes.inventory.ItemHandlerProvider {
*///?} else if <1.21.11 {
/*public class FilterBlockEntity extends TransposerBlockEntity implements WorldlyContainer, MenuProvider, ItemHandlerProvider {
*///?} else if <26.1.2 {
/*public class FilterBlockEntity extends TransposerBlockEntity implements WorldlyContainer, MenuProvider, com.rptubes.inventory.ItemHandlerProvider{
*///?} else {
/*public class FilterBlockEntity extends TransposerBlockEntity implements ItemHandlerProvider, WorldlyContainer, MenuProvider {
*///?}
    private NonNullList<ItemStack> filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
    private MachineLib.FilterMap filterMap;
    private long jammedUntil;
    public byte color = 0;
    //? if neoforge && <1.21.1 {
    /*protected final MachineEnergy energy = new MachineEnergy();

    public IEnergyStorage getEnergyStorage() {
        return this.energy;
    }


    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        if (side == null) return this.handlers[0];
        return this.handlers[side.ordinal()];
    }
    *///?} else if neoforge && <1.21.11 {
    protected final MachineEnergy energy = new MachineEnergy();

    public IEnergyStorage getEnergyStorage() {
        return this.energy;
    }


    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[side.ordinal()];
    }
    //?} else if fabric && >=1.21.11 && <26.1.2 {
    /*protected final MachineEnergy energy = new MachineEnergy();
    private IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    *///?} else if neoforge && >=26.2 {
    /*protected final MachineEnergy energy = new MachineEnergy();

    public MachineEnergy getEnergy() {
        return this.energy;
    }
    public ResourceHandler<ItemResource> getItemHandler(Direction side) {
        return new WorldlyContainerWrapper(this, side);
    }
    *///?} else if fabric && <1.21.1 {
    /*protected final MachineEnergy energy = new MachineEnergy();
    private final IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    *///?} else if neoforge {
    /*protected final MachineEnergy energy = new MachineEnergy();

    public MachineEnergy getEnergy() {
        return this.energy;
    }
    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[side.ordinal()];
    }
    *///?} else if fabric {
    /*protected final MachineEnergy energy = new MachineEnergy();
    private final IItemHandler[] handlers = java.util.Arrays.stream(Direction.values())
            .map(dir -> (IItemHandler) new SidedInvWrapper(this, dir))
            .toArray(IItemHandler[]::new);

    public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[side.ordinal()];
    }
    *///?} else {
    /*private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.values());
    protected final MachineEnergy energy = new MachineEnergy();
    *///?}

    public FilterBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.FILTER.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.FILTER, pos, state);
        *///?}
    }

    protected FilterBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
                                BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private void regenFilterMap() {
        ItemStack[] arr = this.filterSlots.toArray(ItemStack[]::new);
        this.filterMap = MachineLib.makeFilterMap(arr);
    }

    private MachineLib.FilterMap map() {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        return this.filterMap;
    }

    @Override
    protected void addToBuffer(ItemStack stack) {
        this.buffer.addNewColor(stack, this.color & 0xFF);
    }

    @Override
    protected ItemStack extractFrom(BlockPos src, Direction extractSide) {
        MachineLib.FilterMap map = this.map();
        if (map.size() == 0) {
            return MachineLib.collectOneStack(this.level, src, extractSide, ItemStack.EMPTY);
        }
        int slot = MachineLib.matchAnyStack(map, this.level, src, extractSide);
        if (slot < 0) {
            return ItemStack.EMPTY;
        }
        return MachineLib.collectOneStack(this.level, src, extractSide, this.filterSlots.get(slot).copy());
    }

    @Override
    protected boolean suckFilter(ItemStack stack) {
        MachineLib.FilterMap map = this.map();
        return map.size() == 0 || map.containsKey(stack);
    }

    /**
     * Refuse passive tube items and RF auto-extraction for the next few ticks. A Retrievulator
     * calls this every tick while its targets are met, so an emptied filter grid pulls nothing.
     */
    public void jam() {
        if (this.level != null) {
            this.jammedUntil = this.level.getGameTime() + 5;
        }
    }

    protected boolean isJammed() {
        return this.level != null && this.level.getGameTime() < this.jammedUntil;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        Direction back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 1) {
            if (this.isJammed()) {
                return false;
            }
            MachineLib.FilterMap map = this.map();
            if (map.size() > 0 && !map.containsKey(ti.item)) {
                return false;
            }
        }
        return super.tubeItemEnter(side, state, ti);
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        Direction back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 1) {
            if (this.isJammed()) {
                return false;
            }
            MachineLib.FilterMap map = this.map();
            if (map.size() > 0 && !map.containsKey(ti.item)) {
                return false;
            }
        }
        return super.tubeItemCanEnter(side, state, ti);
    }

    //? if fabric && >=1.20.6 {

    /*@Override
    *///?} else {
    @Override
    //?}
    protected boolean payForExtract(boolean mustHaveEnergy) {
        return this.energy.pay(mustHaveEnergy);
    }

    @Override
    protected void tryEnergyAutoExtract() {
        if (this.isJammed() || !this.energy.canAutoExtract()) {
            return;
        }
        if (!this.energy.pay(true)) {
            return;
        }
        this.setActive(true);
        this.startExtract();
        this.tickCooldown = 10;
        this.setChanged();
    //? if fabric {
    /*}

    public MachineEnergy getEnergy() {
        return this.energy;
    *///?}
    }

    public boolean applyColor(int dyeColor) {
        // 1-16 dye colors; 0 = unpainted
        byte next = (byte) ((dyeColor % 16) + 1);
        if (this.color == next) {
            return false;
        }
        this.color = next;
        this.setChanged();
        return true;
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int slot) {
        return this.filterSlots.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.filterSlots, slot, amount);
        if (!result.isEmpty()) {
            this.filterMap = null;
            this.setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.filterMap = null;
        return ContainerHelper.takeItem(this.filterSlots, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.filterSlots.set(slot, stack);
        this.filterMap = null;
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
        this.filterSlots.clear();
        this.filterMap = null;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        Direction face = this.getFacing();
        if (side == face || side == face.getOpposite()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.filter");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FilterMenu(id, inv, this);
    }

    @Override
    //? if <1.20.6 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.filterSlots);
    *///?} else if <1.21.11 {
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.filterSlots, registries);
    //?} else {
    /*protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.filterSlots);
    *///?}
        tag.putByte("color", this.color);
        this.energy.write(tag);
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.filterSlots);
        this.color = tag.getByte("color");
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.filterSlots, registries);
        this.color = tag.getByte("color");
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.filterSlots);
        this.color = tag.getByteOr("color", (byte) 0);
    *///?}
        this.energy.read(tag);
        this.filterMap = null;
    }

    //? if forge {
    /*@Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER && side != null) {
            return handlers[side.ordinal()].cast();
        }
        if (!this.remove && this.energy.isEnergyCap(cap)) {
            return this.energy.cast(cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<? extends IItemHandler> h : handlers) {
            h.invalidate();
        }
        this.energy.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this, Direction.values());
        this.energy.revive();
    }

    @Override
    *///?} else if <1.20.6 {
    /*public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[side.ordinal()];
    }

    @Override
    *///?} else if fabric && <1.21.1 {

    /*public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[side.ordinal()];
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
        //? if <1.21.11 {
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty() && this.level != null && Removal.isBroken(this)) {
                net.minecraft.world.Containers.dropItemStack(this.level,
                        this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
            }
        }
        super.setRemoved();
        //?} else {
        /*// Filter slot drops handled by the default preRemoveSideEffects (Container)
        super.setRemoved();
        *///?}
    }
    //? if fabric && >=1.21.11 && <26.1.2 {

    /*@Override
    public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[side.ordinal()];
    }
    *///?}
}
