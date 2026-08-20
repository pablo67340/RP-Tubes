package com.rptubes.util;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;

public final class FuelHelper {
    private FuelHelper() {
    }

    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Integer time = FuelRegistry.INSTANCE.get(stack.getItem());
        return time == null ? 0 : time;
    }
}
