//? if fabric && <1.21.11 {
/*package com.rptubes.util;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.1 {
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
//?}

public final class FuelHelper {
    private FuelHelper() {
    }

    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Integer time = FuelRegistry.INSTANCE.get(stack.getItem());
        //? if <1.21.1 {
        /^return time == null ? 0 : time;
        ^///?} else {
        if (time != null) {
            return time;
        }
        Integer vanilla = AbstractFurnaceBlockEntity.getFuel().get(stack.getItem());
        return vanilla == null ? 0 : vanilla;
        //?}
    }
}
*///?}
