package com.rptubes.blockentity;

import com.rptubes.block.BatteryBoxBlock;
import com.rptubes.item.BatteryItem;
import com.rptubes.menu.BatteryBoxMenu;
import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBlockEntity;
import com.rptubes.power.PowerBuffer;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Port of TileBatteryBox: 6000 RP2 units (600,000 FE). Takes energy from any side and
 * gives it to every neighbour except other Battery Boxes, up to 10 units (1000 FE) per
 * tick; a redstone signal stops it giving. Slot 0 charges an item from storage, slot 1
 * drains an item into storage, 25 units (2500 FE) per tick each.
 */
public class BatteryBoxBlockEntity extends PowerBlockEntity implements ISidedInventory, INamedContainerProvider {
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

    private final IIntArray data = new IIntArray() {
        @Override
        public int get(int index) {
            BatteryBoxBlockEntity be = BatteryBoxBlockEntity.this;
            switch (index) {
                case 0:
                    return (int) (be.buffer.getStored() / PowerBuffer.FE_PER_UNIT);
                case 1:
                    return be.inRate;
                case 2:
                    return be.outRate;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public BatteryBoxBlockEntity() {
        super(ModBlockEntities.BATTERY_BOX.get(), CAPACITY, MAX_IO, MAX_IO);
    }

    @Override
    protected void serverTick() {
        long external = this.buffer.getStored() - this.lastStored;
        long inflow = Math.max(0, external);
        long outflow = Math.max(0, -external);

        long stored = this.buffer.getStored();
        if (stored > 0) {
            this.buffer.drain(ItemPower.charge(this, SLOT_CHARGE, Math.min(BatteryItem.TRANSFER, stored)));
        }
        long room = CAPACITY - this.buffer.getStored();
        if (room > 0) {
            this.buffer.generate(ItemPower.drain(this, SLOT_DRAIN, Math.min(BatteryItem.TRANSFER, room)));
        }

        boolean powered = this.level.hasNeighborSignal(this.worldPosition);
        this.buffer.setExtractEnabled(!powered);
        if (!powered) {
            outflow += this.pushToNeighbours(MAX_IO);
        }

        this.inRate = (int) Math.min(MAX_IO, inflow);
        this.outRate = (int) Math.min(MAX_IO, outflow);
        this.lastStored = this.buffer.getStored();

        BlockState state = this.getBlockState();
        int charge = (int) (this.buffer.getStored() * 8 / CAPACITY);
        if (state.getValue(BatteryBoxBlock.CHARGE) != charge) {
            this.level.setBlock(this.worldPosition, state.setValue(BatteryBoxBlock.CHARGE, charge), 2);
        }
    }

    @Override
    protected boolean canPushTo(Direction dir) {
        return !(this.level.getBlockEntity(this.worldPosition.relative(dir)) instanceof BatteryBoxBlockEntity);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.rptubes.battery_box");
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
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
        ItemStack removed = ItemStackHelper.removeItem(this.items, slot, amount);
        if (!removed.isEmpty()) {
            this.setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStackHelper.takeItem(this.items, slot);
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
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.lastStored = this.buffer.getStored();
    }
}
