package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.ItemDetectorMenu;
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

/** Port of TileItemDetect — pass-through tube machine that emits redstone when items pass. */
public class ItemDetectorBlockEntity extends TileEntity implements net.minecraft.tileentity.ITickableTileEntity, ITubeConnectable, ISidedInventory, INamedContainerProvider {
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
        super(ModBlockEntities.ITEM_DETECTOR.get());
    }

    public static void serverTick(World level, BlockPos pos, BlockState state, ItemDetectorBlockEntity be) {
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
            this.setChanged();
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
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
            for (Direction dir : Direction.values()) {
                this.level.updateNeighborsAt(this.worldPosition.relative(dir), state.getBlock());
            }
        }
        this.setChanged();
    }

    private void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!MachineLib.handleItem(this.level, ti, this.worldPosition, this.getFacing())) {
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

    public Direction getFacing() {
        return this.getBlockState().getValue(MachineBlock.FACING);
    }

    private void setActive(boolean value) {
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
        Direction face = this.getFacing();
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
        Direction face = this.getFacing();
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
        Direction face = this.getFacing();
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
        Direction face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.rptubes.item_detector");
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new ItemDetectorMenu(id, inv, this);
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
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        ItemStackHelper.saveAllItems(tag, this.filterSlots);
        this.buffer.writeToNBT(tag);
        tag.putBoolean("Active", this.active);
        tag.putBoolean("Emitting", this.emitting);
        tag.putByte("mode", this.mode);
        tag.putInt("cnt", this.count);
        tag.putBoolean("Delay", this.delay);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.filterSlots);
        this.buffer.readFromNBT(tag);
        this.active = tag.getBoolean("Active");
        this.emitting = tag.getBoolean("Emitting");
        this.mode = tag.getByte("mode");
        this.count = tag.getInt("cnt");
        this.delay = tag.getBoolean("Delay");
        this.filterMap = null;
    }

    @Override
    public void setRemoved() {
        this.buffer.onRemove(this);
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty() && this.level != null) {
                net.minecraft.inventory.InventoryHelper.dropItemStack(this.level,
                        this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
            }
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
