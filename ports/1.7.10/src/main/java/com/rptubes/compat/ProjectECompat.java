package com.rptubes.compat;

import com.rptubes.RPTubes;
import com.rptubes.registry.ModItems;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Loader;

import java.lang.reflect.Method;

/**
 * Soft ProjectE EMC registration via reflection (no hard compile dependency).
 * Called from RPTubes init (after registries are populated).
 */
public final class ProjectECompat {
    private ProjectECompat() {
    }

    public static void init() {
        if (!Loader.isModLoaded("projecte")) {
            return;
        }
        registerEmc();
    }

    private static void registerEmc() {
        try {
            Class<?> api = Class.forName("moze_intel.projecte.api.ProjectEAPI");
            Method getProxy = api.getMethod("getEMCProxy");
            Object proxy = getProxy.invoke(null);
            // The 1.7.10 ProjectE API only takes int values.
            Method register = proxy.getClass().getMethod("registerCustomEMC", ItemStack.class, int.class);

            // Tekkit-era ratios: (3 copper @85.33 + 1 tin @256) / 4 = 128 brass
            register.invoke(proxy, new ItemStack(ModItems.TIN_INGOT.get()), 256);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.TIN_ORE.get()), 256);
            register.invoke(proxy, new ItemStack(ModItems.BRASS_INGOT.get()), 128);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.PNEUMATIC_TUBE.get()), 128);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.REDSTONE_TUBE.get()), 192);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.RESTRICTION_TUBE.get()), 256);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.TRANSPOSER.get()), 512);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.FILTER.get()), 1024);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.EJECTOR.get()), 768);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.BLOCK_BREAKER.get()), 768);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.DEPLOYER.get()), 768);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.BUFFER.get()), 512);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.ITEM_DETECTOR.get()), 640);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.RELAY.get()), 896);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.RETRIEVER.get()), 1536);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.REGULATOR.get()), 1280);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.SORTING_MACHINE.get()), 1536);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.IGNITER.get()), 1024);
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.ALLOY_FURNACE.get()), 256);
            // ACT = 4 brass ingots (128 each) + crafting table (32)
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.AUTO_CRAFTING_TABLE.get()), 544);
            // Mk II = ACT + 4 tubes + chest + planks
            register.invoke(proxy, new ItemStack(com.rptubes.registry.ModBlocks.ACT_MK2.get()), 1280);
            RPTubes.LOGGER.info("Registered ProjectE EMC values for RP Tubes items.");
        } catch (ReflectiveOperationException e) {
            RPTubes.LOGGER.warn("ProjectE present but EMC API unavailable: {}", e.toString());
        }
    }
}
