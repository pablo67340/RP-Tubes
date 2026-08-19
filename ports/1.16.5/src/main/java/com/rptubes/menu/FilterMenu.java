package com.rptubes.menu;

import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class FilterMenu extends Container {
    private final FilterBlockEntity filter;
    private final IWorldPosCallable access;

    public FilterMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getFilter(playerInv, buf));
    }

    public FilterMenu(int id, PlayerInventory playerInv, FilterBlockEntity filter) {
        super(ModMenus.FILTER.get(), id);
        this.filter = filter;
        this.access = IWorldPosCallable.create(filter.getLevel(), filter.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(filter, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        // Syncs the paint color to the client for the swatch in the GUI
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return filter.color & 0xFF;
            }

            @Override
            public void set(int value) {
                filter.color = (byte) value;
            }
        });
    }

    public int getColor() {
        return this.filter.color & 0xFF;
    }

    /** Button 0 = next color, 1 = previous (OG GuiFilter left/right click). */
    @Override
    public boolean clickMenuButton(PlayerEntity player, int id) {
        int c = this.filter.color & 0xFF;
        if (id == 0) {
            c = c >= 16 ? 0 : c + 1;
        } else if (id == 1) {
            c = c <= 0 ? 16 : c - 1;
        } else {
            return false;
        }
        this.filter.color = (byte) c;
        this.filter.setChanged();
        return true;
    }

    private static FilterBlockEntity getFilter(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof FilterBlockEntity) {
            FilterBlockEntity filter = (FilterBlockEntity) be;
            return filter;
        }
        throw new IllegalStateException("Filter missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.FILTER.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 9) {
                if (!this.moveItemStackTo(stack, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}
