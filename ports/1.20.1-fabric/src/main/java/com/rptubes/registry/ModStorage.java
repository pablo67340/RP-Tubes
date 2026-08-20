package com.rptubes.registry;

import com.rptubes.inventory.ItemHandlerProvider;
import com.rptubes.inventory.ItemHandlerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public final class ModStorage {
    private ModStorage() {
    }

    public static void register() {
        register(ModBlockEntities.ALLOY_FURNACE);
        register(ModBlockEntities.FILTER);
        register(ModBlockEntities.EJECTOR);
        register(ModBlockEntities.DEPLOYER);
        register(ModBlockEntities.BUFFER);
        register(ModBlockEntities.RELAY);
        register(ModBlockEntities.RETRIEVER);
        register(ModBlockEntities.REGULATOR);
        register(ModBlockEntities.AUTO_CRAFTING_TABLE);
        register(ModBlockEntities.ACT_MK2);
    }

    private static void register(net.minecraft.world.level.block.entity.BlockEntityType<?> type) {
        ItemStorage.SIDED.registerForBlockEntity((be, dir) -> {
            if (be instanceof ItemHandlerProvider provider) {
                return ItemHandlerStorage.of(provider.getItemHandler(dir));
            }
            return null;
        }, type);
    }
}
