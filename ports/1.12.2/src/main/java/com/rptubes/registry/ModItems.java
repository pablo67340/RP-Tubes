package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class ModItems {

    public static final CreativeTabs TAB = ModTab.TAB;

    private static Item named(Item item, String name) {
        item.setRegistryName(new ResourceLocation(RPTubes.MOD_ID, name));
        item.setTranslationKey(RPTubes.MOD_ID + "." + name);
        item.setCreativeTab(TAB);
        return item;
    }

    private static Item blockItem(Block block) {
        ItemBlock item = new ItemBlock(block);
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    public static final Ref<Item> BRASS_INGOT = new Ref<Item>(named(new Item(), "brass_ingot"));
    public static final Ref<Item> SCREWDRIVER = new Ref<Item>(
            named(new com.rptubes.item.ScrewdriverItem(), "screwdriver"));
    public static final Ref<Item> TIN_INGOT = new Ref<Item>(named(new Item(), "tin_ingot"));

    public static final Ref<Item> TIN_ORE = new Ref<Item>(blockItem(ModBlocks.TIN_ORE.get()));
    public static final Ref<Item> ALLOY_FURNACE = new Ref<Item>(blockItem(ModBlocks.ALLOY_FURNACE.get()));
    public static final Ref<Item> TRANSPOSER = new Ref<Item>(blockItem(ModBlocks.TRANSPOSER.get()));
    public static final Ref<Item> FILTER = new Ref<Item>(blockItem(ModBlocks.FILTER.get()));
    public static final Ref<Item> EJECTOR = new Ref<Item>(blockItem(ModBlocks.EJECTOR.get()));
    public static final Ref<Item> BLOCK_BREAKER = new Ref<Item>(blockItem(ModBlocks.BLOCK_BREAKER.get()));
    public static final Ref<Item> DEPLOYER = new Ref<Item>(blockItem(ModBlocks.DEPLOYER.get()));
    public static final Ref<Item> BUFFER = new Ref<Item>(blockItem(ModBlocks.BUFFER.get()));
    public static final Ref<Item> ITEM_DETECTOR = new Ref<Item>(blockItem(ModBlocks.ITEM_DETECTOR.get()));
    public static final Ref<Item> RELAY = new Ref<Item>(blockItem(ModBlocks.RELAY.get()));
    public static final Ref<Item> RETRIEVER = new Ref<Item>(blockItem(ModBlocks.RETRIEVER.get()));
    public static final Ref<Item> REGULATOR = new Ref<Item>(blockItem(ModBlocks.REGULATOR.get()));
    public static final Ref<Item> SORTING_MACHINE = new Ref<Item>(blockItem(ModBlocks.SORTING_MACHINE.get()));
    public static final Ref<Item> IGNITER = new Ref<Item>(blockItem(ModBlocks.IGNITER.get()));
    public static final Ref<Item> AUTO_CRAFTING_TABLE = new Ref<Item>(blockItem(ModBlocks.AUTO_CRAFTING_TABLE.get()));
    public static final Ref<Item> ACT_MK2 = new Ref<Item>(blockItem(ModBlocks.ACT_MK2.get()));
    public static final Ref<Item> PNEUMATIC_TUBE = new Ref<Item>(blockItem(ModBlocks.PNEUMATIC_TUBE.get()));
    public static final Ref<Item> REDSTONE_TUBE = new Ref<Item>(blockItem(ModBlocks.REDSTONE_TUBE.get()));
    public static final Ref<Item> RESTRICTION_TUBE = new Ref<Item>(blockItem(ModBlocks.RESTRICTION_TUBE.get()));

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                BRASS_INGOT.get(), SCREWDRIVER.get(), TIN_INGOT.get(),
                TIN_ORE.get(), ALLOY_FURNACE.get(), TRANSPOSER.get(), FILTER.get(), EJECTOR.get(),
                BLOCK_BREAKER.get(), DEPLOYER.get(), BUFFER.get(), ITEM_DETECTOR.get(), RELAY.get(),
                RETRIEVER.get(), REGULATOR.get(), SORTING_MACHINE.get(), IGNITER.get(),
                AUTO_CRAFTING_TABLE.get(), ACT_MK2.get(),
                PNEUMATIC_TUBE.get(), REDSTONE_TUBE.get(), RESTRICTION_TUBE.get());

        // ore dictionary — replaces the forge item tags used on newer versions
        OreDictionary.registerOre("ingotTin", TIN_INGOT.get());
        OreDictionary.registerOre("ingotBrass", BRASS_INGOT.get());
        OreDictionary.registerOre("oreTin", ModBlocks.TIN_ORE.get());
    }

    private ModItems() {
    }
}
