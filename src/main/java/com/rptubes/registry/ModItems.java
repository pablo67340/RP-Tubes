package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RPTubes.MOD_ID);

    public static final CreativeModeTab TAB = new CreativeModeTab(RPTubes.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(PNEUMATIC_TUBE.get());
        }
    };

    public static final RegistryObject<Item> BRASS_INGOT = ITEMS.register("brass_ingot",
            () -> new Item(new Item.Properties().tab(TAB)));

    public static final RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new com.rptubes.item.ScrewdriverItem(new Item.Properties().tab(TAB)));

    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> TIN_ORE = ITEMS.register("tin_ore",
            () -> new BlockItem(ModBlocks.TIN_ORE.get(), new Item.Properties().tab(TAB)));

    public static final RegistryObject<Item> ALLOY_FURNACE = ITEMS.register("alloy_furnace",
            () -> new BlockItem(ModBlocks.ALLOY_FURNACE.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> TRANSPOSER = ITEMS.register("transposer",
            () -> new BlockItem(ModBlocks.TRANSPOSER.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> FILTER = ITEMS.register("filter",
            () -> new BlockItem(ModBlocks.FILTER.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> EJECTOR = ITEMS.register("ejector",
            () -> new BlockItem(ModBlocks.EJECTOR.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> BLOCK_BREAKER = ITEMS.register("block_breaker",
            () -> new BlockItem(ModBlocks.BLOCK_BREAKER.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> DEPLOYER = ITEMS.register("deployer",
            () -> new BlockItem(ModBlocks.DEPLOYER.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> BUFFER = ITEMS.register("buffer",
            () -> new BlockItem(ModBlocks.BUFFER.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> ITEM_DETECTOR = ITEMS.register("item_detector",
            () -> new BlockItem(ModBlocks.ITEM_DETECTOR.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> RELAY = ITEMS.register("relay",
            () -> new BlockItem(ModBlocks.RELAY.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> RETRIEVER = ITEMS.register("retriever",
            () -> new BlockItem(ModBlocks.RETRIEVER.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> REGULATOR = ITEMS.register("regulator",
            () -> new BlockItem(ModBlocks.REGULATOR.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SORTING_MACHINE = ITEMS.register("sorting_machine",
            () -> new BlockItem(ModBlocks.SORTING_MACHINE.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> IGNITER = ITEMS.register("igniter",
            () -> new BlockItem(ModBlocks.IGNITER.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> AUTO_CRAFTING_TABLE = ITEMS.register("auto_crafting_table",
            () -> new BlockItem(ModBlocks.AUTO_CRAFTING_TABLE.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> ACT_MK2 = ITEMS.register("act_mk2",
            () -> new BlockItem(ModBlocks.ACT_MK2.get(), new Item.Properties().tab(TAB)));

    public static final RegistryObject<Item> PNEUMATIC_TUBE = ITEMS.register("pneumatic_tube",
            () -> new BlockItem(ModBlocks.PNEUMATIC_TUBE.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> REDSTONE_TUBE = ITEMS.register("redstone_tube",
            () -> new BlockItem(ModBlocks.REDSTONE_TUBE.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> RESTRICTION_TUBE = ITEMS.register("restriction_tube",
            () -> new BlockItem(ModBlocks.RESTRICTION_TUBE.get(), new Item.Properties().tab(TAB)));

    private ModItems() {
    }
}
