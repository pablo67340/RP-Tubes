package com.rptubes.compat;

import com.rptubes.RPTubes;
import com.rptubes.registry.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

/**
 * Soft ProjectE EMC registration via reflection (no hard compile dependency).
 */
public final class ProjectECompat {
    private ProjectECompat() {
    }

    public static void tryRegister() {
        if (!FabricLoader.getInstance().isModLoaded("projecte")) {
            return;
        }
        registerEmc();
    }

    private static void registerEmc() {
        try {
            Class<?> api = Class.forName("moze_intel.projecte.api.ProjectEAPI");
            Method getProxy = api.getMethod("getEMCProxy");
            Object proxy = getProxy.invoke(null);
            Method register = proxy.getClass().getMethod("registerCustomEMC", ItemStack.class, long.class);

            register.invoke(proxy, new ItemStack(ModItems.TIN_INGOT), 256L);
            register.invoke(proxy, new ItemStack(ModItems.TIN_ORE), 256L);
            register.invoke(proxy, new ItemStack(ModItems.BRASS_INGOT), 128L);
            register.invoke(proxy, new ItemStack(ModItems.PNEUMATIC_TUBE), 128L);
            register.invoke(proxy, new ItemStack(ModItems.REDSTONE_TUBE), 192L);
            register.invoke(proxy, new ItemStack(ModItems.RESTRICTION_TUBE), 256L);
            register.invoke(proxy, new ItemStack(ModItems.TRANSPOSER), 512L);
            register.invoke(proxy, new ItemStack(ModItems.FILTER), 1024L);
            register.invoke(proxy, new ItemStack(ModItems.EJECTOR), 768L);
            register.invoke(proxy, new ItemStack(ModItems.BLOCK_BREAKER), 768L);
            register.invoke(proxy, new ItemStack(ModItems.DEPLOYER), 768L);
            register.invoke(proxy, new ItemStack(ModItems.BUFFER), 512L);
            register.invoke(proxy, new ItemStack(ModItems.ITEM_DETECTOR), 640L);
            register.invoke(proxy, new ItemStack(ModItems.RELAY), 896L);
            register.invoke(proxy, new ItemStack(ModItems.RETRIEVER), 1536L);
            register.invoke(proxy, new ItemStack(ModItems.REGULATOR), 1280L);
            register.invoke(proxy, new ItemStack(ModItems.SORTING_MACHINE), 1536L);
            register.invoke(proxy, new ItemStack(ModItems.IGNITER), 1024L);
            register.invoke(proxy, new ItemStack(ModItems.ALLOY_FURNACE), 256L);
            register.invoke(proxy, new ItemStack(ModItems.AUTO_CRAFTING_TABLE), 544L);
            register.invoke(proxy, new ItemStack(ModItems.ACT_MK2), 1280L);
            RPTubes.LOGGER.info("Registered ProjectE EMC values for RP Tubes items.");
        } catch (ReflectiveOperationException e) {
            RPTubes.LOGGER.warn("ProjectE present but EMC API unavailable: {}", e.toString());
        }
    }
}
