package com.rptubes.power;

import com.rptubes.item.BatteryItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Charges and drains items: RP Tubes batteries directly, anything else through the loader's item energy API. */
public final class ItemPower {
    private ItemPower() {
    }

    public static long charge(Container container, int slot, long amount) {
        ItemStack stack = container.getItem(slot);
        if (stack.isEmpty() || amount <= 0) {
            return 0;
        }
        if (stack.getItem() instanceof BatteryItem) {
            long moved = BatteryItem.receive(stack, amount);
            if (moved > 0) {
                container.setChanged();
            }
            return moved;
        }
        return EnergyBridge.chargeSlot(container, slot, amount);
    }

    public static long drain(Container container, int slot, long amount) {
        ItemStack stack = container.getItem(slot);
        if (stack.isEmpty() || amount <= 0) {
            return 0;
        }
        if (stack.getItem() instanceof BatteryItem) {
            long moved = BatteryItem.extract(stack, amount);
            if (moved > 0) {
                container.setChanged();
            }
            return moved;
        }
        return EnergyBridge.drainSlot(container, slot, amount);
    }

    public static long chargePlayer(Player player, int slot, long amount) {
        ItemStack stack = player.getInventory().getItem(slot);
        if (stack.isEmpty() || amount <= 0) {
            return 0;
        }
        if (stack.getItem() instanceof BatteryItem) {
            return BatteryItem.receive(stack, amount);
        }
        return EnergyBridge.chargePlayerSlot(player, slot, amount);
    }
}
