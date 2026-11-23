package com.rptubes.compat;

//? if forge && <1.21.11 {
/*import com.rptubes.RPTubes;
import com.rptubes.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.Method;

/^*
 * Soft ProjectE EMC registration via reflection (no hard compile dependency).
 ^/
@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
*///?} else if neoforge && <1.21.1 {
/*import com.rptubes.RPTubes;
import com.rptubes.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.Method;

/^*
 * Soft ProjectE EMC registration via reflection (no hard compile dependency).
 ^/
@EventBusSubscriber(modid = RPTubes.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
*///?} else if neoforge {
import com.rptubes.RPTubes;
import com.rptubes.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.Method;

/**
 * Soft ProjectE EMC registration via reflection (no hard compile dependency).
 */
@EventBusSubscriber(modid = RPTubes.MOD_ID)
//?} else if forge {
/*import com.rptubes.RPTubes;
import com.rptubes.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.Method;

/^*
 * Soft ProjectE EMC registration via reflection (no hard compile dependency).
 ^/
@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
*///?} else if <1.21.11 || >=26.1.2 {
/*import com.rptubes.RPTubes;
import com.rptubes.registry.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

/^*
 * Soft ProjectE EMC registration via reflection (no hard compile dependency).
 ^/
*///?}
public final class ProjectECompat {
    private ProjectECompat() {
    }
    //? if forge && >=26.1.2 {

    /*@SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (!ModList.isLoaded("projecte")) {
            return;
        }
        event.enqueueWork(ProjectECompat::registerEmc);
    }

    private static void registerEmc() {
        try {
            Class<?> api = Class.forName("moze_intel.projecte.api.ProjectEAPI");
            Method getProxy = api.getMethod("getEMCProxy");
            Object proxy = getProxy.invoke(null);
            Method register = proxy.getClass().getMethod("registerCustomEMC", ItemStack.class, long.class);

            // Tekkit-era ratios: (3 copper @85.33 + 1 tin @256) / 4 = 128 brass
            register.invoke(proxy, new ItemStack(ModItems.TIN_INGOT.get()), 256L);
            register.invoke(proxy, new ItemStack(ModItems.TIN_ORE.get()), 256L);
            register.invoke(proxy, new ItemStack(ModItems.BRASS_INGOT.get()), 128L);
            register.invoke(proxy, new ItemStack(ModItems.PNEUMATIC_TUBE.get()), 128L);
            register.invoke(proxy, new ItemStack(ModItems.REDSTONE_TUBE.get()), 192L);
            register.invoke(proxy, new ItemStack(ModItems.RESTRICTION_TUBE.get()), 256L);
            register.invoke(proxy, new ItemStack(ModItems.TRANSPOSER.get()), 512L);
            register.invoke(proxy, new ItemStack(ModItems.FILTER.get()), 1024L);
            register.invoke(proxy, new ItemStack(ModItems.EJECTOR.get()), 768L);
            register.invoke(proxy, new ItemStack(ModItems.BLOCK_BREAKER.get()), 768L);
            register.invoke(proxy, new ItemStack(ModItems.DEPLOYER.get()), 768L);
            register.invoke(proxy, new ItemStack(ModItems.BUFFER.get()), 512L);
            register.invoke(proxy, new ItemStack(ModItems.ITEM_DETECTOR.get()), 640L);
            register.invoke(proxy, new ItemStack(ModItems.RELAY.get()), 896L);
            register.invoke(proxy, new ItemStack(ModItems.RETRIEVER.get()), 1536L);
            register.invoke(proxy, new ItemStack(ModItems.REGULATOR.get()), 1280L);
            register.invoke(proxy, new ItemStack(ModItems.SORTING_MACHINE.get()), 1536L);
            register.invoke(proxy, new ItemStack(ModItems.IGNITER.get()), 1024L);
            register.invoke(proxy, new ItemStack(ModItems.ALLOY_FURNACE.get()), 256L);
            // ACT = 4 brass ingots (128 each) + crafting table (32)
            register.invoke(proxy, new ItemStack(ModItems.AUTO_CRAFTING_TABLE.get()), 544L);
            // Mk II = ACT + 4 tubes + chest + planks
            register.invoke(proxy, new ItemStack(ModItems.ACT_MK2.get()), 1280L);
            RPTubes.LOGGER.info("Registered ProjectE EMC values for RP Tubes items.");
        } catch (ReflectiveOperationException e) {
            RPTubes.LOGGER.warn("ProjectE present but EMC API unavailable: {}", e.toString());
        }
    }
    *///?} else if forgelike {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("projecte")) {
            return;
        }
        event.enqueueWork(ProjectECompat::registerEmc);
    }

    private static void registerEmc() {
        try {
            Class<?> api = Class.forName("moze_intel.projecte.api.ProjectEAPI");
            Method getProxy = api.getMethod("getEMCProxy");
            Object proxy = getProxy.invoke(null);
            Method register = proxy.getClass().getMethod("registerCustomEMC", ItemStack.class, long.class);

            // Tekkit-era ratios: (3 copper @85.33 + 1 tin @256) / 4 = 128 brass
            register.invoke(proxy, new ItemStack(ModItems.TIN_INGOT.get()), 256L);
            register.invoke(proxy, new ItemStack(ModItems.TIN_ORE.get()), 256L);
            register.invoke(proxy, new ItemStack(ModItems.BRASS_INGOT.get()), 128L);
            register.invoke(proxy, new ItemStack(ModItems.PNEUMATIC_TUBE.get()), 128L);
            register.invoke(proxy, new ItemStack(ModItems.REDSTONE_TUBE.get()), 192L);
            register.invoke(proxy, new ItemStack(ModItems.RESTRICTION_TUBE.get()), 256L);
            register.invoke(proxy, new ItemStack(ModItems.TRANSPOSER.get()), 512L);
            register.invoke(proxy, new ItemStack(ModItems.FILTER.get()), 1024L);
            register.invoke(proxy, new ItemStack(ModItems.EJECTOR.get()), 768L);
            register.invoke(proxy, new ItemStack(ModItems.BLOCK_BREAKER.get()), 768L);
            register.invoke(proxy, new ItemStack(ModItems.DEPLOYER.get()), 768L);
            register.invoke(proxy, new ItemStack(ModItems.BUFFER.get()), 512L);
            register.invoke(proxy, new ItemStack(ModItems.ITEM_DETECTOR.get()), 640L);
            register.invoke(proxy, new ItemStack(ModItems.RELAY.get()), 896L);
            register.invoke(proxy, new ItemStack(ModItems.RETRIEVER.get()), 1536L);
            register.invoke(proxy, new ItemStack(ModItems.REGULATOR.get()), 1280L);
            register.invoke(proxy, new ItemStack(ModItems.SORTING_MACHINE.get()), 1536L);
            register.invoke(proxy, new ItemStack(ModItems.IGNITER.get()), 1024L);
            register.invoke(proxy, new ItemStack(ModItems.ALLOY_FURNACE.get()), 256L);
            // ACT = 4 brass ingots (128 each) + crafting table (32)
            register.invoke(proxy, new ItemStack(ModItems.AUTO_CRAFTING_TABLE.get()), 544L);
            // Mk II = ACT + 4 tubes + chest + planks
            register.invoke(proxy, new ItemStack(ModItems.ACT_MK2.get()), 1280L);
            RPTubes.LOGGER.info("Registered ProjectE EMC values for RP Tubes items.");
        } catch (ReflectiveOperationException e) {
            RPTubes.LOGGER.warn("ProjectE present but EMC API unavailable: {}", e.toString());
        }
    }
    //?} else if <1.21.11 || >=26.1.2 {

    /*public static void tryRegister() {
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
    *///?}
}
