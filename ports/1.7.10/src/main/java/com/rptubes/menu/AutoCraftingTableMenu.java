package com.rptubes.menu;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Port of BuildCraft ContainerAutoWorkbench: output slot at 124,35, real-item
 * 3x3 grid at 30,17 — the vanilla crafting table layout. Taking the output
 * crafts from the grid (removeRecipe=true, like the original SlotAutoCrafting).
 */
public class AutoCraftingTableMenu extends com.rptubes.util.SyncedContainer {
    private final AutoCraftingTableBlockEntity table;
    private final InventoryCraftResult result = new InventoryCraftResult();

    public AutoCraftingTableMenu(InventoryPlayer playerInv, AutoCraftingTableBlockEntity table) {
        this.table = table;

        this.addSlotToContainer(new ResultSlot(this.result, 0, 124, 35));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(table, col + row * 3, 30 + col * 18, 17 + row * 18));
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
        this.refreshResult();
    }

    private void refreshResult() {
        this.result.setInventorySlotContents(0, this.table.findRecipe());
    }

    @Override
    public void detectAndSendChanges() {
        // The grid can change from automation or neighbors at any time,
        // so keep the preview current every sync tick (like OG detectAndSendChanges).
        this.refreshResult();
        super.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 10, 46, true)) {
                    return null;
                }
                slot.onSlotChange(stack, result);
            } else if (index < 10) {
                if (!this.mergeItemStack(stack, 10, 46, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 1, 10, false)) {
                return null;
            }
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            if (stack.stackSize == result.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(player, stack);
        }
        return result;
    }

    /** Port of SlotAutoCrafting — read-only preview whose take performs the craft. */
    private class ResultSlot extends Slot {
        ResultSlot(InventoryCraftResult container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            return !com.rptubes.util.Stacks.isEmpty(AutoCraftingTableMenu.this.table.findRecipe());
        }

        @Override
        public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
            // GUI crafting consumes grid items outright (OG extractItem(true, true))
            AutoCraftingTableMenu.this.table.craftOne(true, true);
            AutoCraftingTableMenu.this.refreshResult();
            super.onPickupFromSlot(player, stack);
        }
    }
}
