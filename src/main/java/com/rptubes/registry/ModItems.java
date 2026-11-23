package com.rptubes.registry;

import com.rptubes.RPTubes;
//? if forgelike && >=1.21.11 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
*///?} else if >=26.1.2 {
/*import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
*///?} else if >=1.21.11 {
/*import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
*///?} else if fabric {
/*import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
*///?} else if >=1.20.1 {
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
//?}
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if forge && <1.21.11 {
/*import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?} else if neoforge && <1.21.11 {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} else if forge {
/*import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
*///?} else if neoforge {
/*import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
*///?} else if >=1.21.11 {
/*import net.minecraft.world.level.block.Block;
*///?}

public final class ModItems {
    //? if <1.20.1 {
    /*public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RPTubes.MOD_ID);

    public static final CreativeModeTab TAB = new CreativeModeTab(RPTubes.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(PNEUMATIC_TUBE.get());
        }
    };

    private static Item.Properties props() {
        return new Item.Properties().tab(TAB);
    }

    public static final RegistryObject<Item> BRASS_INGOT = ITEMS.register("brass_ingot",
            () -> new Item(props()));
    public static final RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new com.rptubes.item.ScrewdriverItem(props()));
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(props()));
    public static final RegistryObject<Item> TIN_ORE = ITEMS.register("tin_ore",
            () -> new BlockItem(ModBlocks.TIN_ORE.get(), props()));
    public static final RegistryObject<Item> ALLOY_FURNACE = ITEMS.register("alloy_furnace",
            () -> new BlockItem(ModBlocks.ALLOY_FURNACE.get(), props()));
    public static final RegistryObject<Item> TRANSPOSER = ITEMS.register("transposer",
            () -> new BlockItem(ModBlocks.TRANSPOSER.get(), props()));
    public static final RegistryObject<Item> FILTER = ITEMS.register("filter",
            () -> new BlockItem(ModBlocks.FILTER.get(), props()));
    public static final RegistryObject<Item> EJECTOR = ITEMS.register("ejector",
            () -> new BlockItem(ModBlocks.EJECTOR.get(), props()));
    public static final RegistryObject<Item> BLOCK_BREAKER = ITEMS.register("block_breaker",
            () -> new BlockItem(ModBlocks.BLOCK_BREAKER.get(), props()));
    public static final RegistryObject<Item> DEPLOYER = ITEMS.register("deployer",
            () -> new BlockItem(ModBlocks.DEPLOYER.get(), props()));
    public static final RegistryObject<Item> BUFFER = ITEMS.register("buffer",
            () -> new BlockItem(ModBlocks.BUFFER.get(), props()));
    public static final RegistryObject<Item> ITEM_DETECTOR = ITEMS.register("item_detector",
            () -> new BlockItem(ModBlocks.ITEM_DETECTOR.get(), props()));
    public static final RegistryObject<Item> RELAY = ITEMS.register("relay",
            () -> new BlockItem(ModBlocks.RELAY.get(), props()));
    public static final RegistryObject<Item> RETRIEVER = ITEMS.register("retriever",
            () -> new BlockItem(ModBlocks.RETRIEVER.get(), props()));
    public static final RegistryObject<Item> REGULATOR = ITEMS.register("regulator",
            () -> new BlockItem(ModBlocks.REGULATOR.get(), props()));
    public static final RegistryObject<Item> SORTING_MACHINE = ITEMS.register("sorting_machine",
            () -> new BlockItem(ModBlocks.SORTING_MACHINE.get(), props()));
    public static final RegistryObject<Item> IGNITER = ITEMS.register("igniter",
            () -> new BlockItem(ModBlocks.IGNITER.get(), props()));
    public static final RegistryObject<Item> AUTO_CRAFTING_TABLE = ITEMS.register("auto_crafting_table",
            () -> new BlockItem(ModBlocks.AUTO_CRAFTING_TABLE.get(), props()));
    public static final RegistryObject<Item> ACT_MK2 = ITEMS.register("act_mk2",
            () -> new BlockItem(ModBlocks.ACT_MK2.get(), props()));
    public static final RegistryObject<Item> PNEUMATIC_TUBE = ITEMS.register("pneumatic_tube",
            () -> new BlockItem(ModBlocks.PNEUMATIC_TUBE.get(), props()));
    public static final RegistryObject<Item> REDSTONE_TUBE = ITEMS.register("redstone_tube",
            () -> new BlockItem(ModBlocks.REDSTONE_TUBE.get(), props()));
    public static final RegistryObject<Item> RESTRICTION_TUBE = ITEMS.register("restriction_tube",
            () -> new BlockItem(ModBlocks.RESTRICTION_TUBE.get(), props()));
    *///?} else if fabric && >=1.21.1 && <1.21.11 {
    /*public static final Item BRASS_INGOT = register("brass_ingot", new Item(new Item.Properties()));
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
    *///?} else if fabric && >=1.21.1 && <26.1.2 {
    /*// 1.21.2+: every Item requires its registry id on the Properties before construction.
    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
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
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), item);
    }

    public static void init() {
    }
    *///?} else if fabric && <1.21.1 {
    /*public static final Item BRASS_INGOT = register("brass_ingot", new Item(new Item.Properties()));
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
            new ResourceLocation(RPTubes.MOD_ID, "main"),
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
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(RPTubes.MOD_ID, name), item);
    }

    public static void init() {
    }
    *///?} else if neoforge && <1.21.11 {
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
    //?} else if fabric {
    /*private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
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
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "main"),
            FabricCreativeModeTab.builder()
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
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), item);
    }

    public static void init() {
    }
    *///?} else if <1.21.11 {
    /*public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RPTubes.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RPTubes.MOD_ID);

    public static final RegistryObject<Item> BRASS_INGOT = ITEMS.register("brass_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new com.rptubes.item.ScrewdriverItem(new Item.Properties()));

    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIN_ORE = ITEMS.register("tin_ore",
            () -> new BlockItem(ModBlocks.TIN_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ALLOY_FURNACE = ITEMS.register("alloy_furnace",
            () -> new BlockItem(ModBlocks.ALLOY_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> TRANSPOSER = ITEMS.register("transposer",
            () -> new BlockItem(ModBlocks.TRANSPOSER.get(), new Item.Properties()));
    public static final RegistryObject<Item> FILTER = ITEMS.register("filter",
            () -> new BlockItem(ModBlocks.FILTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> EJECTOR = ITEMS.register("ejector",
            () -> new BlockItem(ModBlocks.EJECTOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_BREAKER = ITEMS.register("block_breaker",
            () -> new BlockItem(ModBlocks.BLOCK_BREAKER.get(), new Item.Properties()));
    public static final RegistryObject<Item> DEPLOYER = ITEMS.register("deployer",
            () -> new BlockItem(ModBlocks.DEPLOYER.get(), new Item.Properties()));
    public static final RegistryObject<Item> BUFFER = ITEMS.register("buffer",
            () -> new BlockItem(ModBlocks.BUFFER.get(), new Item.Properties()));
    public static final RegistryObject<Item> ITEM_DETECTOR = ITEMS.register("item_detector",
            () -> new BlockItem(ModBlocks.ITEM_DETECTOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> RELAY = ITEMS.register("relay",
            () -> new BlockItem(ModBlocks.RELAY.get(), new Item.Properties()));
    public static final RegistryObject<Item> RETRIEVER = ITEMS.register("retriever",
            () -> new BlockItem(ModBlocks.RETRIEVER.get(), new Item.Properties()));
    public static final RegistryObject<Item> REGULATOR = ITEMS.register("regulator",
            () -> new BlockItem(ModBlocks.REGULATOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> SORTING_MACHINE = ITEMS.register("sorting_machine",
            () -> new BlockItem(ModBlocks.SORTING_MACHINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> IGNITER = ITEMS.register("igniter",
            () -> new BlockItem(ModBlocks.IGNITER.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTO_CRAFTING_TABLE = ITEMS.register("auto_crafting_table",
            () -> new BlockItem(ModBlocks.AUTO_CRAFTING_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ACT_MK2 = ITEMS.register("act_mk2",
            () -> new BlockItem(ModBlocks.ACT_MK2.get(), new Item.Properties()));

    public static final RegistryObject<Item> PNEUMATIC_TUBE = ITEMS.register("pneumatic_tube",
            () -> new BlockItem(ModBlocks.PNEUMATIC_TUBE.get(), new Item.Properties()));
    public static final RegistryObject<Item> REDSTONE_TUBE = ITEMS.register("redstone_tube",
            () -> new BlockItem(ModBlocks.REDSTONE_TUBE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RESTRICTION_TUBE = ITEMS.register("restriction_tube",
            () -> new BlockItem(ModBlocks.RESTRICTION_TUBE.get(), new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
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
    *///?} else if forge {
    /*public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RPTubes.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RPTubes.MOD_ID);

    // 1.21.2+: every Item requires its registry id on the Properties before construction.
    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
    }

    private static RegistryObject<Item> blockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name,
                () -> new BlockItem(block.get(), props(name).useBlockDescriptionPrefix()));
    }

    public static final RegistryObject<Item> BRASS_INGOT = ITEMS.register("brass_ingot",
            () -> new Item(props("brass_ingot")));

    public static final RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new com.rptubes.item.ScrewdriverItem(props("screwdriver")));

    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(props("tin_ingot")));
    public static final RegistryObject<Item> TIN_ORE = blockItem("tin_ore", ModBlocks.TIN_ORE);

    public static final RegistryObject<Item> ALLOY_FURNACE = blockItem("alloy_furnace", ModBlocks.ALLOY_FURNACE);
    public static final RegistryObject<Item> TRANSPOSER = blockItem("transposer", ModBlocks.TRANSPOSER);
    public static final RegistryObject<Item> FILTER = blockItem("filter", ModBlocks.FILTER);
    public static final RegistryObject<Item> EJECTOR = blockItem("ejector", ModBlocks.EJECTOR);
    public static final RegistryObject<Item> BLOCK_BREAKER = blockItem("block_breaker", ModBlocks.BLOCK_BREAKER);
    public static final RegistryObject<Item> DEPLOYER = blockItem("deployer", ModBlocks.DEPLOYER);
    public static final RegistryObject<Item> BUFFER = blockItem("buffer", ModBlocks.BUFFER);
    public static final RegistryObject<Item> ITEM_DETECTOR = blockItem("item_detector", ModBlocks.ITEM_DETECTOR);
    public static final RegistryObject<Item> RELAY = blockItem("relay", ModBlocks.RELAY);
    public static final RegistryObject<Item> RETRIEVER = blockItem("retriever", ModBlocks.RETRIEVER);
    public static final RegistryObject<Item> REGULATOR = blockItem("regulator", ModBlocks.REGULATOR);
    public static final RegistryObject<Item> SORTING_MACHINE = blockItem("sorting_machine", ModBlocks.SORTING_MACHINE);
    public static final RegistryObject<Item> IGNITER = blockItem("igniter", ModBlocks.IGNITER);
    public static final RegistryObject<Item> AUTO_CRAFTING_TABLE = blockItem("auto_crafting_table", ModBlocks.AUTO_CRAFTING_TABLE);
    public static final RegistryObject<Item> ACT_MK2 = blockItem("act_mk2", ModBlocks.ACT_MK2);

    public static final RegistryObject<Item> PNEUMATIC_TUBE = blockItem("pneumatic_tube", ModBlocks.PNEUMATIC_TUBE);
    public static final RegistryObject<Item> REDSTONE_TUBE = blockItem("redstone_tube", ModBlocks.REDSTONE_TUBE);
    public static final RegistryObject<Item> RESTRICTION_TUBE = blockItem("restriction_tube", ModBlocks.RESTRICTION_TUBE);

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
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
    *///?} else {
    /*public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, RPTubes.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RPTubes.MOD_ID);

    // 1.21.2+: every Item requires its registry id on the Properties before construction.
    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
    }

    private static DeferredHolder<Item, Item> blockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name,
                () -> new BlockItem(block.get(), props(name).useBlockDescriptionPrefix()));
    }

    public static final DeferredHolder<Item, Item> BRASS_INGOT = ITEMS.register("brass_ingot",
            () -> new Item(props("brass_ingot")));

    public static final DeferredHolder<Item, Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new com.rptubes.item.ScrewdriverItem(props("screwdriver")));

    public static final DeferredHolder<Item, Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(props("tin_ingot")));
    public static final DeferredHolder<Item, Item> TIN_ORE = blockItem("tin_ore", ModBlocks.TIN_ORE);

    public static final DeferredHolder<Item, Item> ALLOY_FURNACE = blockItem("alloy_furnace", ModBlocks.ALLOY_FURNACE);
    public static final DeferredHolder<Item, Item> TRANSPOSER = blockItem("transposer", ModBlocks.TRANSPOSER);
    public static final DeferredHolder<Item, Item> FILTER = blockItem("filter", ModBlocks.FILTER);
    public static final DeferredHolder<Item, Item> EJECTOR = blockItem("ejector", ModBlocks.EJECTOR);
    public static final DeferredHolder<Item, Item> BLOCK_BREAKER = blockItem("block_breaker", ModBlocks.BLOCK_BREAKER);
    public static final DeferredHolder<Item, Item> DEPLOYER = blockItem("deployer", ModBlocks.DEPLOYER);
    public static final DeferredHolder<Item, Item> BUFFER = blockItem("buffer", ModBlocks.BUFFER);
    public static final DeferredHolder<Item, Item> ITEM_DETECTOR = blockItem("item_detector", ModBlocks.ITEM_DETECTOR);
    public static final DeferredHolder<Item, Item> RELAY = blockItem("relay", ModBlocks.RELAY);
    public static final DeferredHolder<Item, Item> RETRIEVER = blockItem("retriever", ModBlocks.RETRIEVER);
    public static final DeferredHolder<Item, Item> REGULATOR = blockItem("regulator", ModBlocks.REGULATOR);
    public static final DeferredHolder<Item, Item> SORTING_MACHINE = blockItem("sorting_machine", ModBlocks.SORTING_MACHINE);
    public static final DeferredHolder<Item, Item> IGNITER = blockItem("igniter", ModBlocks.IGNITER);
    public static final DeferredHolder<Item, Item> AUTO_CRAFTING_TABLE = blockItem("auto_crafting_table", ModBlocks.AUTO_CRAFTING_TABLE);
    public static final DeferredHolder<Item, Item> ACT_MK2 = blockItem("act_mk2", ModBlocks.ACT_MK2);

    public static final DeferredHolder<Item, Item> PNEUMATIC_TUBE = blockItem("pneumatic_tube", ModBlocks.PNEUMATIC_TUBE);
    public static final DeferredHolder<Item, Item> REDSTONE_TUBE = blockItem("redstone_tube", ModBlocks.REDSTONE_TUBE);
    public static final DeferredHolder<Item, Item> RESTRICTION_TUBE = blockItem("restriction_tube", ModBlocks.RESTRICTION_TUBE);

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
    *///?}

    private ModItems() {
    }
}
