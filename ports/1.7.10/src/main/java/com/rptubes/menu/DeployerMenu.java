package com.rptubes.menu;

import com.rptubes.blockentity.DeployerBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class DeployerMenu extends com.rptubes.util.SyncedContainer {

    public DeployerMenu(InventoryPlayer playerInv, DeployerBlockEntity deployer) {

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(deployer, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        addPlayerSlots(playerInv, 84);
    }

    private void addPlayerSlots(InventoryPlayer playerInv, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, y + 58));
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
