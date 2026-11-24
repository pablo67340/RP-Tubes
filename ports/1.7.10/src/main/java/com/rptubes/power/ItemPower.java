package com.rptubes.power;

import com.rptubes.item.BatteryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** Charges and drains items: RP Tubes batteries directly, anything else through the RF API. */
public final class ItemPower {
    private ItemPower() {
    }

    public static long charge(IInventory container, int slot, long amount) {
        ItemStack stack = container.getStackInSlot(slot);
        if (stack == null || amount <= 0) {
            return 0;
        }
        long moved = stack.getItem() instanceof BatteryItem
                ? BatteryItem.receive(stack, amount) : RfCompat.chargeItem(stack, amount);
        if (moved > 0) {
            container.markDirty();
        }
        return moved;
    }

    public static long drain(IInventory container, int slot, long amount) {
        ItemStack stack = container.getStackInSlot(slot);
        if (stack == null || amount <= 0) {
            return 0;
        }
        long moved = stack.getItem() instanceof BatteryItem
                ? BatteryItem.extract(stack, amount) : RfCompat.drainItem(stack, amount);
        if (moved > 0) {
            container.markDirty();
        }
        return moved;
    }

    public static long chargePlayer(EntityPlayer player, int slot, long amount) {
        return charge(player.inventory, slot, amount);
    }
}
