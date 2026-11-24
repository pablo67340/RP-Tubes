package com.rptubes.registry;

import com.rptubes.RPTubes;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ModItems {

    private static Item named(Item item, String name) {
        item.setUnlocalizedName(RPTubes.MOD_ID + "." + name);
        item.setTextureName("rptubes:" + name);
        item.setCreativeTab(ModTab.TAB);
        return item;
    }

    public static final Ref<Item> BRASS_INGOT = new Ref<Item>(named(new Item(), "brass_ingot"));
    public static final Ref<Item> SCREWDRIVER = new Ref<Item>(
            named(new com.rptubes.item.ScrewdriverItem(), "screwdriver"));
    public static final Ref<Item> TIN_INGOT = new Ref<Item>(named(new Item(), "tin_ingot"));
    public static final Ref<Item> COPPER_INGOT = new Ref<Item>(named(new Item(), "copper_ingot"));
    public static final Ref<Item> BATTERY = new Ref<Item>(named(new com.rptubes.item.BatteryItem(), "battery"));

    public static void register() {
        GameRegistry.registerItem(BRASS_INGOT.get(), "brass_ingot");
        GameRegistry.registerItem(SCREWDRIVER.get(), "screwdriver");
        GameRegistry.registerItem(TIN_INGOT.get(), "tin_ingot");
        GameRegistry.registerItem(COPPER_INGOT.get(), "copper_ingot");
        GameRegistry.registerItem(BATTERY.get(), "battery");

        // ore dictionary — replaces the forge item tags used on newer versions
        OreDictionary.registerOre("ingotTin", new ItemStack(TIN_INGOT.get()));
        OreDictionary.registerOre("ingotBrass", new ItemStack(BRASS_INGOT.get()));
        OreDictionary.registerOre("oreTin", new ItemStack(ModBlocks.TIN_ORE.get()));
        OreDictionary.registerOre("ingotCopper", new ItemStack(COPPER_INGOT.get()));
        OreDictionary.registerOre("oreCopper", new ItemStack(ModBlocks.COPPER_ORE.get()));
    }

    private ModItems() {
    }
}
