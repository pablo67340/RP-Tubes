package com.rptubes.blockentity;

import com.rptubes.inventory.InvWrapper;

import com.rptubes.inventory.ItemHandlerProvider;

import com.rptubes.inventory.IItemHandler;

import com.rptubes.menu.BufferMenu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Port of TileBufferChest — 20-slot storage accessible from all sides. */
public class BufferBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, ItemHandlerProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(20, ItemStack.EMPTY);
    private final IItemHandler handler = new InvWrapper(this);
    private static final int[] ALL_SLOTS = new int[20];

    static {
        for (int i = 0; i < 20; i++) {
            ALL_SLOTS[i] = i;
        }
    }

    public BufferBlockEntity() {
        super(ModBlockEntities.BUFFER);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.rptubes.buffer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new BufferMenu(id, inv, this);
    }

    @Override
    public int getContainerSize() {
        return 20;
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
        return ALL_SLOTS;
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
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.items = NonNullList.withSize(20, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    }




    public IItemHandler getItemHandler(Direction side) {
        return this.handler;
    }

    @Override
    public void setRemoved() {
        if (this.level != null) {
            net.minecraft.world.Containers.dropContents(this.level, this.worldPosition, this);
        }
        super.setRemoved();
    }
}
