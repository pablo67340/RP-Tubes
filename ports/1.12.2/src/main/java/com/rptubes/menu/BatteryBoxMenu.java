package com.rptubes.menu;

import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.power.PowerBuffer;
import com.rptubes.util.IIntArray;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** OG ContainerBatteryBox: charge slot at (120, 27), drain slot at (120, 55), 170 px tall. */
public class BatteryBoxMenu extends com.rptubes.util.SyncedContainer {
    public static final int MAX_STORAGE = (int) (BatteryBoxBlockEntity.CAPACITY / PowerBuffer.FE_PER_UNIT);
    private final BatteryBoxBlockEntity box;
    private final IIntArray data;

    public BatteryBoxMenu(InventoryPlayer playerInv, BatteryBoxBlockEntity box) {
        this.box = box;
        this.data = box.getData();

        this.addSlotToContainer(new Slot(box, BatteryBoxBlockEntity.SLOT_CHARGE, 120, 27));
        this.addSlotToContainer(new Slot(box, BatteryBoxBlockEntity.SLOT_DRAIN, 120, 55));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 88 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 146));
        }
        this.addDataSlots(this.data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.box.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < 2) {
                if (!this.mergeItemStack(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, 2, false)) {
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

    /** Stored energy in RP2 units, 0 to {@link #MAX_STORAGE}. */
    public int getStorage() {
        return this.data.get(0);
    }

    public int getInRate() {
        return this.data.get(1);
    }

    public int getOutRate() {
        return this.data.get(2);
    }
}
