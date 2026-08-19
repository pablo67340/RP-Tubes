package com.rptubes.menu;

import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IntReferenceHolder;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class FilterMenu extends com.rptubes.util.SyncedContainer {
    private final FilterBlockEntity filter;

    public FilterMenu(InventoryPlayer playerInv, FilterBlockEntity filter) {
        this.filter = filter;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(filter, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
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
    public boolean enchantItem(EntityPlayer player, int id) {
        int c = this.filter.color & 0xFF;
        if (id == 0) {
            c = c >= 16 ? 0 : c + 1;
        } else if (id == 1) {
            c = c <= 0 ? 16 : c - 1;
        } else {
            return false;
        }
        this.filter.color = (byte) c;
        this.filter.markDirty();
        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < 9) {
                if (!this.mergeItemStack(stack, 9, 45, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 0, 9, false)) {
                return null;
            }
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }
        return result;
    }
}
