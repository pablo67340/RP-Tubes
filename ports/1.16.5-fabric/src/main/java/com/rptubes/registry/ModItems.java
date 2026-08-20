package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

public final class ModItems {
    public static final CreativeModeTab TAB = FabricItemGroupBuilder.build(
            new ResourceLocation(RPTubes.MOD_ID, "main"),
            () -> new ItemStack(Registry.ITEM.get(new ResourceLocation(RPTubes.MOD_ID, "pneumatic_tube"))));

    public static final Item BRASS_INGOT = register("brass_ingot", new Item(new Item.Properties().tab(TAB)));
    public static final Item SCREWDRIVER = register("screwdriver",
            new com.rptubes.item.ScrewdriverItem(new Item.Properties().tab(TAB)));
    public static final Item TIN_INGOT = register("tin_ingot", new Item(new Item.Properties().tab(TAB)));
    public static final Item TIN_ORE = register("tin_ore", new BlockItem(ModBlocks.TIN_ORE, new Item.Properties().tab(TAB)));
    public static final Item ALLOY_FURNACE = register("alloy_furnace", new BlockItem(ModBlocks.ALLOY_FURNACE, new Item.Properties().tab(TAB)));
    public static final Item TRANSPOSER = register("transposer", new BlockItem(ModBlocks.TRANSPOSER, new Item.Properties().tab(TAB)));
    public static final Item FILTER = register("filter", new BlockItem(ModBlocks.FILTER, new Item.Properties().tab(TAB)));
    public static final Item EJECTOR = register("ejector", new BlockItem(ModBlocks.EJECTOR, new Item.Properties().tab(TAB)));
    public static final Item BLOCK_BREAKER = register("block_breaker", new BlockItem(ModBlocks.BLOCK_BREAKER, new Item.Properties().tab(TAB)));
    public static final Item DEPLOYER = register("deployer", new BlockItem(ModBlocks.DEPLOYER, new Item.Properties().tab(TAB)));
    public static final Item BUFFER = register("buffer", new BlockItem(ModBlocks.BUFFER, new Item.Properties().tab(TAB)));
    public static final Item ITEM_DETECTOR = register("item_detector", new BlockItem(ModBlocks.ITEM_DETECTOR, new Item.Properties().tab(TAB)));
    public static final Item RELAY = register("relay", new BlockItem(ModBlocks.RELAY, new Item.Properties().tab(TAB)));
    public static final Item RETRIEVER = register("retriever", new BlockItem(ModBlocks.RETRIEVER, new Item.Properties().tab(TAB)));
    public static final Item REGULATOR = register("regulator", new BlockItem(ModBlocks.REGULATOR, new Item.Properties().tab(TAB)));
    public static final Item SORTING_MACHINE = register("sorting_machine", new BlockItem(ModBlocks.SORTING_MACHINE, new Item.Properties().tab(TAB)));
    public static final Item IGNITER = register("igniter", new BlockItem(ModBlocks.IGNITER, new Item.Properties().tab(TAB)));
    public static final Item AUTO_CRAFTING_TABLE = register("auto_crafting_table", new BlockItem(ModBlocks.AUTO_CRAFTING_TABLE, new Item.Properties().tab(TAB)));
    public static final Item ACT_MK2 = register("act_mk2", new BlockItem(ModBlocks.ACT_MK2, new Item.Properties().tab(TAB)));
    public static final Item PNEUMATIC_TUBE = register("pneumatic_tube", new BlockItem(ModBlocks.PNEUMATIC_TUBE, new Item.Properties().tab(TAB)));
    public static final Item REDSTONE_TUBE = register("redstone_tube", new BlockItem(ModBlocks.REDSTONE_TUBE, new Item.Properties().tab(TAB)));
    public static final Item RESTRICTION_TUBE = register("restriction_tube", new BlockItem(ModBlocks.RESTRICTION_TUBE, new Item.Properties().tab(TAB)));

    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(RPTubes.MOD_ID, name), item);
    }

    public static void init() {
    }

    private ModItems() {
    }
}
