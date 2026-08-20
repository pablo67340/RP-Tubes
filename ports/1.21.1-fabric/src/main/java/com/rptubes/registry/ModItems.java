package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModItems {
    public static final Item BRASS_INGOT = register("brass_ingot", new Item(new Item.Properties()));
    public static final Item SCREWDRIVER = register("screwdriver",
            new com.rptubes.item.ScrewdriverItem(new Item.Properties()));
    public static final Item TIN_INGOT = register("tin_ingot", new Item(new Item.Properties()));
    public static final Item TIN_ORE = register("tin_ore", new BlockItem(ModBlocks.TIN_ORE, new Item.Properties()));
    public static final Item ALLOY_FURNACE = register("alloy_furnace", new BlockItem(ModBlocks.ALLOY_FURNACE, new Item.Properties()));
    public static final Item TRANSPOSER = register("transposer", new BlockItem(ModBlocks.TRANSPOSER, new Item.Properties()));
    public static final Item FILTER = register("filter", new BlockItem(ModBlocks.FILTER, new Item.Properties()));
    public static final Item EJECTOR = register("ejector", new BlockItem(ModBlocks.EJECTOR, new Item.Properties()));
    public static final Item BLOCK_BREAKER = register("block_breaker", new BlockItem(ModBlocks.BLOCK_BREAKER, new Item.Properties()));
    public static final Item DEPLOYER = register("deployer", new BlockItem(ModBlocks.DEPLOYER, new Item.Properties()));
    public static final Item BUFFER = register("buffer", new BlockItem(ModBlocks.BUFFER, new Item.Properties()));
    public static final Item ITEM_DETECTOR = register("item_detector", new BlockItem(ModBlocks.ITEM_DETECTOR, new Item.Properties()));
    public static final Item RELAY = register("relay", new BlockItem(ModBlocks.RELAY, new Item.Properties()));
    public static final Item RETRIEVER = register("retriever", new BlockItem(ModBlocks.RETRIEVER, new Item.Properties()));
    public static final Item REGULATOR = register("regulator", new BlockItem(ModBlocks.REGULATOR, new Item.Properties()));
    public static final Item SORTING_MACHINE = register("sorting_machine", new BlockItem(ModBlocks.SORTING_MACHINE, new Item.Properties()));
    public static final Item IGNITER = register("igniter", new BlockItem(ModBlocks.IGNITER, new Item.Properties()));
    public static final Item AUTO_CRAFTING_TABLE = register("auto_crafting_table", new BlockItem(ModBlocks.AUTO_CRAFTING_TABLE, new Item.Properties()));
    public static final Item ACT_MK2 = register("act_mk2", new BlockItem(ModBlocks.ACT_MK2, new Item.Properties()));
    public static final Item PNEUMATIC_TUBE = register("pneumatic_tube", new BlockItem(ModBlocks.PNEUMATIC_TUBE, new Item.Properties()));
    public static final Item REDSTONE_TUBE = register("redstone_tube", new BlockItem(ModBlocks.REDSTONE_TUBE, new Item.Properties()));
    public static final Item RESTRICTION_TUBE = register("restriction_tube", new BlockItem(ModBlocks.RESTRICTION_TUBE, new Item.Properties()));

    public static final CreativeModeTab TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "main"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup." + RPTubes.MOD_ID))
                    .icon(() -> new ItemStack(PNEUMATIC_TUBE))
                    .displayItems((params, output) -> {
                        output.accept(BRASS_INGOT);
                        output.accept(TIN_INGOT);
                        output.accept(TIN_ORE);
                        output.accept(SCREWDRIVER);
                        output.accept(PNEUMATIC_TUBE);
                        output.accept(REDSTONE_TUBE);
                        output.accept(RESTRICTION_TUBE);
                        output.accept(ALLOY_FURNACE);
                        output.accept(TRANSPOSER);
                        output.accept(FILTER);
                        output.accept(EJECTOR);
                        output.accept(BLOCK_BREAKER);
                        output.accept(DEPLOYER);
                        output.accept(BUFFER);
                        output.accept(ITEM_DETECTOR);
                        output.accept(RELAY);
                        output.accept(RETRIEVER);
                        output.accept(REGULATOR);
                        output.accept(SORTING_MACHINE);
                        output.accept(IGNITER);
                        output.accept(AUTO_CRAFTING_TABLE);
                        output.accept(ACT_MK2);
                    })
                    .build());

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), item);
    }

    public static void init() {
    }

    private ModItems() {
    }
}
