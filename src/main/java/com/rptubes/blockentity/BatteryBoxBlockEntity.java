package com.rptubes.blockentity;

import com.rptubes.block.BatteryBoxBlock;
import com.rptubes.item.BatteryItem;
import com.rptubes.menu.BatteryBoxMenu;
import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBlockEntity;
import com.rptubes.power.PowerBuffer;
import com.rptubes.registry.ModBlockEntities;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Port of TileBatteryBox: 6000 RP2 units (600,000 FE). Takes energy from any side and
 * gives it to every neighbour except other Battery Boxes, up to 10 units (1000 FE) per
 * tick; a redstone signal stops it giving. Slot 0 charges an item from storage, slot 1
 * drains an item into storage, 25 units (2500 FE) per tick each.
 */
public class BatteryBoxBlockEntity extends PowerBlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SLOT_CHARGE = 0;
    public static final int SLOT_DRAIN = 1;
    public static final long CAPACITY = 6000L * PowerBuffer.FE_PER_UNIT;
    public static final long MAX_IO = 10L * PowerBuffer.FE_PER_UNIT;
    private static final int[] TOP = {SLOT_CHARGE};
    private static final int[] BOTTOM = {SLOT_DRAIN};
    private static final int[] NONE = {};

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private long lastStored;
    private int inRate;
    private int outRate;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            BatteryBoxBlockEntity be = BatteryBoxBlockEntity.this;
            return switch (index) {
                case 0 -> (int) (be.buffer.getStored() / PowerBuffer.FE_PER_UNIT);
                case 1 -> be.inRate;
                case 2 -> be.outRate;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public BatteryBoxBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.BATTERY_BOX.get(), pos, state, CAPACITY, MAX_IO, MAX_IO);
        //?} else {
        /*super(ModBlockEntities.BATTERY_BOX, pos, state, CAPACITY, MAX_IO, MAX_IO);
        *///?}
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BatteryBoxBlockEntity be) {
        long external = be.buffer.getStored() - be.lastStored;
        long inflow = Math.max(0, external);
        long outflow = Math.max(0, -external);

        long stored = be.buffer.getStored();
        if (stored > 0) {
            be.buffer.drain(ItemPower.charge(be, SLOT_CHARGE, Math.min(BatteryItem.TRANSFER, stored)));
        }
        long room = CAPACITY - be.buffer.getStored();
        if (room > 0) {
            be.buffer.generate(ItemPower.drain(be, SLOT_DRAIN, Math.min(BatteryItem.TRANSFER, room)));
        }

        boolean powered = level.hasNeighborSignal(pos);
        be.buffer.setExtractEnabled(!powered);
        if (!powered) {
            outflow += be.pushToNeighbours(MAX_IO);
        }

        be.inRate = (int) Math.min(MAX_IO, inflow);
        be.outRate = (int) Math.min(MAX_IO, outflow);
        be.lastStored = be.buffer.getStored();

        int charge = (int) (be.buffer.getStored() * 8 / CAPACITY);
        if (state.getValue(BatteryBoxBlock.CHARGE) != charge) {
            level.setBlock(pos, state.setValue(BatteryBoxBlock.CHARGE, charge), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected boolean canPushTo(Direction dir) {
        return !(this.level.getBlockEntity(this.worldPosition.relative(dir)) instanceof BatteryBoxBlockEntity);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.battery_box");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new BatteryBoxMenu(id, inv, this, this.data);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
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
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);
        if (!removed.isEmpty()) {
            this.setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
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
        return side == Direction.UP ? TOP : side == Direction.DOWN ? BOTTOM : NONE;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return !(stack.getItem() instanceof BatteryItem) || BatteryItem.getCharge(stack) == 0;
    }

    @Override
    //? if <1.20.6 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
    *///?} else if <1.21.11 {
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    //?} else {
    /*protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
    *///?}
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    *///?}
        this.lastStored = this.buffer.getStored();
    }
}
