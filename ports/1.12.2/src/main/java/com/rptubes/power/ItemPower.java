package com.rptubes.power;

import com.rptubes.item.BatteryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** Charges and drains items: RP Tubes batteries directly, anything else through Forge Energy. */
public final class ItemPower {
    private ItemPower() {
    }

    public static long charge(IInventory container, int slot, long amount) {
        ItemStack stack = container.getStackInSlot(slot);
        if (stack.isEmpty() || amount <= 0) {
            return 0;
        }
        if (stack.getItem() instanceof BatteryItem) {
            long moved = BatteryItem.receive(stack, amount);
            if (moved > 0) {
                container.markDirty();
            }
            return moved;
        }
        return EnergyBridge.chargeSlot(container, slot, amount);
    }

    public static long drain(IInventory container, int slot, long amount) {
        ItemStack stack = container.getStackInSlot(slot);
        if (stack.isEmpty() || amount <= 0) {
            return 0;
        }
        if (stack.getItem() instanceof BatteryItem) {
            long moved = BatteryItem.extract(stack, amount);
            if (moved > 0) {
                container.markDirty();
            }
            return moved;
        }
        return EnergyBridge.drainSlot(container, slot, amount);
    }

    public static long chargePlayer(EntityPlayer player, int slot, long amount) {
        ItemStack stack = player.inventory.getStackInSlot(slot);
        if (stack.isEmpty() || amount <= 0) {
            return 0;
        }
        if (stack.getItem() instanceof BatteryItem) {
            return BatteryItem.receive(stack, amount);
        }
        return EnergyBridge.chargePlayerSlot(player, slot, amount);
    }
}
