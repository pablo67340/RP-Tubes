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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RPTubes.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RPTubes.MOD_ID);

    // 1.21.2+: every Item requires its registry id on the Properties before construction.
    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
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

    private ModItems() {
    }
}
