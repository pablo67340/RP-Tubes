package com.rptubes.menu;

import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.registry.ModBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IntReferenceHolder;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ItemDetectorMenu extends com.rptubes.util.SyncedContainer {
    private final ItemDetectorBlockEntity detector;

    public ItemDetectorMenu(InventoryPlayer playerInv, ItemDetectorBlockEntity detector) {
        this.detector = detector;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(detector, col + row * 3, 62 + col * 18, 17 + row * 18));
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

        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return detector.mode;
            }

            @Override
            public void set(int value) {
                detector.mode = (byte) value;
            }
        });
    }

    public int getMode() {
        return this.detector.mode;
    }

    /** 0 = next mode, 1 = previous (OG GuiItemDetect left/right click). */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        int m = this.detector.mode;
        if (id == 0) {
            m = m >= 2 ? 0 : m + 1;
        } else if (id == 1) {
            m = m <= 0 ? 2 : m - 1;
        } else {
            return false;
        }
        this.detector.mode = (byte) m;
        this.detector.markDirty();
        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < 9) {
                if (!this.mergeItemStack(stack, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return result;
    }
}
