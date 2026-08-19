package com.rptubes.menu;

import com.rptubes.blockentity.BufferBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class BufferMenu extends com.rptubes.util.SyncedContainer {

    public BufferMenu(InventoryPlayer playerInv, BufferBlockEntity buffer) {

        // OG ContainerBufferChest: 5 columns x 4 rows, column-major slot indices
        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 4; row++) {
                this.addSlotToContainer(new Slot(buffer, row + col * 4, 44 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 162));
        }
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
            if (index < 20) {
                if (!this.mergeItemStack(stack, 20, 56, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 0, 20, false)) {
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
