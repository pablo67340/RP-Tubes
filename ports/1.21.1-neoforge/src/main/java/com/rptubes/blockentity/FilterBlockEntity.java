package com.rptubes.blockentity;

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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

/**
 * Port of TileFilter — stack extract + optional filter slots + paint color.
 */
public class FilterBlockEntity extends TransposerBlockEntity implements WorldlyContainer, MenuProvider {
    private NonNullList<ItemStack> filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
    private MachineLib.FilterMap filterMap;
    public byte color = 0;


    private final IItemHandler[] handlers = new IItemHandler[] {
            new SidedInvWrapper(this, Direction.NORTH),
            new SidedInvWrapper(this, Direction.SOUTH),
            new SidedInvWrapper(this, Direction.WEST)
    };

    public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[Math.abs(side.ordinal() % this.handlers.length)];
    }

    public FilterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FILTER.get(), pos, state);
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
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.filterSlots, registries);
        tag.putByte("color", this.color);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.filterSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.filterSlots, registries);
        this.color = tag.getByte("color");
        this.filterMap = null;
    }


    @Override
    public void setRemoved() {
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty() && this.level != null) {
                net.minecraft.world.Containers.dropItemStack(this.level,
                        this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
            }
        }
        super.setRemoved();
    }
}
