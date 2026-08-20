package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, RPTubes.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RPTubes.MOD_ID);

    public static final DeferredHolder<Item, Item> BRASS_INGOT = ITEMS.register("brass_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new com.rptubes.item.ScrewdriverItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> TIN_ORE = ITEMS.register("tin_ore",
            () -> new BlockItem(ModBlocks.TIN_ORE.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> ALLOY_FURNACE = ITEMS.register("alloy_furnace",
            () -> new BlockItem(ModBlocks.ALLOY_FURNACE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> TRANSPOSER = ITEMS.register("transposer",
            () -> new BlockItem(ModBlocks.TRANSPOSER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> FILTER = ITEMS.register("filter",
            () -> new BlockItem(ModBlocks.FILTER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> EJECTOR = ITEMS.register("ejector",
            () -> new BlockItem(ModBlocks.EJECTOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> BLOCK_BREAKER = ITEMS.register("block_breaker",
            () -> new BlockItem(ModBlocks.BLOCK_BREAKER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> DEPLOYER = ITEMS.register("deployer",
            () -> new BlockItem(ModBlocks.DEPLOYER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> BUFFER = ITEMS.register("buffer",
            () -> new BlockItem(ModBlocks.BUFFER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> ITEM_DETECTOR = ITEMS.register("item_detector",
            () -> new BlockItem(ModBlocks.ITEM_DETECTOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> RELAY = ITEMS.register("relay",
            () -> new BlockItem(ModBlocks.RELAY.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> RETRIEVER = ITEMS.register("retriever",
            () -> new BlockItem(ModBlocks.RETRIEVER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> REGULATOR = ITEMS.register("regulator",
            () -> new BlockItem(ModBlocks.REGULATOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> SORTING_MACHINE = ITEMS.register("sorting_machine",
            () -> new BlockItem(ModBlocks.SORTING_MACHINE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> IGNITER = ITEMS.register("igniter",
            () -> new BlockItem(ModBlocks.IGNITER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> AUTO_CRAFTING_TABLE = ITEMS.register("auto_crafting_table",
            () -> new BlockItem(ModBlocks.AUTO_CRAFTING_TABLE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> ACT_MK2 = ITEMS.register("act_mk2",
            () -> new BlockItem(ModBlocks.ACT_MK2.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> PNEUMATIC_TUBE = ITEMS.register("pneumatic_tube",
            () -> new BlockItem(ModBlocks.PNEUMATIC_TUBE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> REDSTONE_TUBE = ITEMS.register("redstone_tube",
            () -> new BlockItem(ModBlocks.REDSTONE_TUBE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> RESTRICTION_TUBE = ITEMS.register("restriction_tube",
            () -> new BlockItem(ModBlocks.RESTRICTION_TUBE.get(), new Item.Properties()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + RPTubes.MOD_ID))
                    .icon(() -> new ItemStack(PNEUMATIC_TUBE.get()))
                    .displayItems((params, output) -> {
                        output.accept(BRASS_INGOT.get());
                        output.accept(TIN_INGOT.get());
                        output.accept(TIN_ORE.get());
                        output.accept(SCREWDRIVER.get());
                        output.accept(PNEUMATIC_TUBE.get());
                        output.accept(REDSTONE_TUBE.get());
                        output.accept(RESTRICTION_TUBE.get());
                        output.accept(ALLOY_FURNACE.get());
                        output.accept(TRANSPOSER.get());
                        output.accept(FILTER.get());
                        output.accept(EJECTOR.get());
                        output.accept(BLOCK_BREAKER.get());
                        output.accept(DEPLOYER.get());
                        output.accept(BUFFER.get());
                        output.accept(ITEM_DETECTOR.get());
                        output.accept(RELAY.get());
                        output.accept(RETRIEVER.get());
                        output.accept(REGULATOR.get());
                        output.accept(SORTING_MACHINE.get());
                        output.accept(IGNITER.get());
                        output.accept(AUTO_CRAFTING_TABLE.get());
                        output.accept(ACT_MK2.get());
                    })
                    .build());

    private ModItems() {
    }
}
