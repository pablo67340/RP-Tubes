package com.rptubes.blockentity;

import com.rptubes.menu.FilterMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.MachineLib;
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
import net.minecraft.block.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Port of TileFilter — stack extract + optional filter slots + paint color.
 */
public class FilterBlockEntity extends TransposerBlockEntity implements ISidedInventory, INamedContainerProvider {
    private NonNullList<ItemStack> filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
    private MachineLib.FilterMap filterMap;
    public byte color = 0;
    private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.NORTH, Direction.SOUTH, Direction.WEST);
    protected final MachineEnergy energy = new MachineEnergy();

    public FilterBlockEntity() {
        super(ModBlockEntities.FILTER.get());
    }

    protected FilterBlockEntity(net.minecraft.tileentity.TileEntityType<?> type) {
        super(type);
    }

    private void regenFilterMap() {
        ItemStack[] arr = this.filterSlots.toArray(new ItemStack[0]);
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

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        Direction back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 1) {
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
            MachineLib.FilterMap map = this.map();
            if (map.size() > 0 && !map.containsKey(ti.item)) {
                return false;
            }
        }
        return super.tubeItemCanEnter(side, state, ti);
    }

    @Override
    protected boolean payForExtract(boolean mustHaveEnergy) {
        return this.energy.pay(mustHaveEnergy);
    }

    @Override
    protected void tryEnergyAutoExtract() {
        if (!this.energy.canAutoExtract()) {
            return;
        }
        if (!this.energy.pay(true)) {
            return;
        }
        this.setActive(true);
        this.startExtract();
        this.tickCooldown = 10;
        this.setChanged();
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
        ItemStack result = ItemStackHelper.removeItem(this.filterSlots, slot, amount);
        if (!result.isEmpty()) {
            this.filterMap = null;
            this.setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.filterMap = null;
        return ItemStackHelper.takeItem(this.filterSlots, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.filterSlots.set(slot, stack);
        this.filterMap = null;
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
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.rptubes.filter");
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new FilterMenu(id, inv, this);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        ItemStackHelper.saveAllItems(tag, this.filterSlots);
        tag.putByte("color", this.color);
        this.energy.write(tag);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.filterSlots);
        this.color = tag.getByte("color");
        this.energy.read(tag);
        this.filterMap = null;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null) {
            return handlers[Math.abs(side.ordinal() % handlers.length)].cast();
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
        this.handlers = SidedInvWrapper.create(this, Direction.NORTH, Direction.SOUTH, Direction.WEST);
        this.energy.revive();
    }

    @Override
    public void setRemoved() {
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty() && this.level != null) {
                net.minecraft.inventory.InventoryHelper.dropItemStack(this.level,
                        this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
            }
        }
        super.setRemoved();
    }
}
