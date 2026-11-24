package com.rptubes.menu;

import com.rptubes.blockentity.RetrievulatorBlockEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static com.rptubes.blockentity.RetrievulatorBlockEntity.GRID;
import static com.rptubes.blockentity.RetrievulatorBlockEntity.MAX_TARGET;

/**
 * R grid (real stacks, the amount fetched per pulse) on the left, T grid (ghost
 * targets) on the right. On a T square, right click adds one, left click removes
 * one, shift steps by ten, and clicking with a different stack sets it.
 */
public class RetrievulatorMenu extends com.rptubes.util.SyncedContainer {
    // Menu slot indices: 0-8 R grid, 9-17 T grid, 18-53 player
    private static final int TARGET_START = GRID;
    private static final int PLAYER_START = GRID * 2;
    private static final int PLAYER_END = PLAYER_START + 36;
    // 1.7.10 slotClick modes
    private static final int MODE_PICKUP = 0;
    private static final int MODE_QUICK_MOVE = 1;

    private final RetrievulatorBlockEntity retrievulator;

    public RetrievulatorMenu(InventoryPlayer playerInv, RetrievulatorBlockEntity retrievulator) {
        this.retrievulator = retrievulator;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(retrievulator, col + row * 3, 26 + col * 18, 18 + row * 18));
            }
        }
        IInventory targets = retrievulator.getTargetView();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new GhostSlot(targets, col + row * 3, 98 + col * 18, 18 + row * 18));
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
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.retrievulator.isUseableByPlayer(player);
    }

    @Override
    public ItemStack slotClick(int slotId, int button, int mode, EntityPlayer player) {
        boolean pickup = mode == MODE_PICKUP;
        boolean shift = mode == MODE_QUICK_MOVE;
        if (slotId >= TARGET_START && slotId < PLAYER_START && (pickup || shift)) {
            int t = slotId - TARGET_START;
            ItemStack carried = player.inventory.getItemStack();
            ItemStack target = this.retrievulator.getTarget(t);
            int count = this.retrievulator.getTargetCount(t);
            if (carried != null && (target == null || !RetrievulatorBlockEntity.sameItem(carried, target))) {
                this.retrievulator.setTarget(t, carried, carried.stackSize);
            } else if (target != null) {
                int step = shift ? 10 : 1;
                if (button == 1) {
                    this.retrievulator.setTarget(t, target, Math.min(MAX_TARGET, count + step));
                } else if (button == 0) {
                    this.retrievulator.setTarget(t, target, count - step);
                }
            }
            return null;
        }
        return super.slotClick(slotId, button, mode, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index >= TARGET_START && index < PLAYER_START) {
            return null;
        }
        ItemStack result = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < TARGET_START) {
                if (!this.mergeItemStack(stack, PLAYER_START, PLAYER_END, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 0, TARGET_START, false)) {
                return null;
            }
            if (stack.stackSize <= 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }
        return result;
    }

    private static class GhostSlot extends Slot {
        GhostSlot(IInventory targets, int slot, int x, int y) {
            super(targets, slot, x, y);
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            return false;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public int getSlotStackLimit() {
            return MAX_TARGET;
        }
    }
}
