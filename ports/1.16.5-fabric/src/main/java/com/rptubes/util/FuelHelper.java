package com.rptubes.util;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public final class FuelHelper {
    private FuelHelper() {
    }

    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Integer fabric = FuelRegistry.INSTANCE.get(stack.getItem());
        if (fabric != null && fabric > 0) {
            return fabric;
        }
        Integer vanilla = AbstractFurnaceBlockEntity.getFuel().get(stack.getItem());
        return vanilla == null ? 0 : vanilla;
    }
}
