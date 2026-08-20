package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, RPTubes.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RPTubes.MOD_ID);

    // 1.21.2+: every Item requires its registry id on the Properties before construction.
    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
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

    private ModItems() {
    }
}
