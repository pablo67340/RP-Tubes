package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class ModItems {
    // 1.21.2+: every Item requires its registry id on the Properties before construction.
    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
    }

    private static Item blockItem(String name, Block block) {
        return register(name, new BlockItem(block, props(name).useBlockDescriptionPrefix()));
    }

    public static final Item BRASS_INGOT = register("brass_ingot", new Item(props("brass_ingot")));
    public static final Item SCREWDRIVER = register("screwdriver",
            new com.rptubes.item.ScrewdriverItem(props("screwdriver")));
    public static final Item TIN_INGOT = register("tin_ingot", new Item(props("tin_ingot")));
    public static final Item TIN_ORE = blockItem("tin_ore", ModBlocks.TIN_ORE);
    public static final Item ALLOY_FURNACE = blockItem("alloy_furnace", ModBlocks.ALLOY_FURNACE);
    public static final Item TRANSPOSER = blockItem("transposer", ModBlocks.TRANSPOSER);
    public static final Item FILTER = blockItem("filter", ModBlocks.FILTER);
    public static final Item EJECTOR = blockItem("ejector", ModBlocks.EJECTOR);
    public static final Item BLOCK_BREAKER = blockItem("block_breaker", ModBlocks.BLOCK_BREAKER);
    public static final Item DEPLOYER = blockItem("deployer", ModBlocks.DEPLOYER);
    public static final Item BUFFER = blockItem("buffer", ModBlocks.BUFFER);
    public static final Item ITEM_DETECTOR = blockItem("item_detector", ModBlocks.ITEM_DETECTOR);
    public static final Item RELAY = blockItem("relay", ModBlocks.RELAY);
    public static final Item RETRIEVER = blockItem("retriever", ModBlocks.RETRIEVER);
    public static final Item REGULATOR = blockItem("regulator", ModBlocks.REGULATOR);
    public static final Item SORTING_MACHINE = blockItem("sorting_machine", ModBlocks.SORTING_MACHINE);
    public static final Item IGNITER = blockItem("igniter", ModBlocks.IGNITER);
    public static final Item AUTO_CRAFTING_TABLE = blockItem("auto_crafting_table", ModBlocks.AUTO_CRAFTING_TABLE);
    public static final Item ACT_MK2 = blockItem("act_mk2", ModBlocks.ACT_MK2);
    public static final Item PNEUMATIC_TUBE = blockItem("pneumatic_tube", ModBlocks.PNEUMATIC_TUBE);
    public static final Item REDSTONE_TUBE = blockItem("redstone_tube", ModBlocks.REDSTONE_TUBE);
    public static final Item RESTRICTION_TUBE = blockItem("restriction_tube", ModBlocks.RESTRICTION_TUBE);

    public static final CreativeModeTab TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, "main"),
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
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name), item);
    }

    public static void init() {
    }

    private ModItems() {
    }
}
