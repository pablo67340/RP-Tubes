package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.menu.AutoCraftingTableMenu;
import com.rptubes.menu.AutoCraftingTableMk2Menu;
import com.rptubes.menu.BufferMenu;
import com.rptubes.menu.DeployerMenu;
import com.rptubes.menu.EjectorMenu;
import com.rptubes.menu.FilterMenu;
import com.rptubes.menu.ItemDetectorMenu;
import com.rptubes.menu.RegulatorMenu;
import com.rptubes.menu.RelayMenu;
import com.rptubes.menu.RetrieverMenu;
import com.rptubes.menu.SortingMachineMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.ResourceLocation;

public final class ModMenus {
    public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "alloy_furnace"), AlloyFurnaceMenu::new);
    public static final MenuType<FilterMenu> FILTER =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "filter"), FilterMenu::new);
    public static final MenuType<EjectorMenu> EJECTOR =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "ejector"), EjectorMenu::new);
    public static final MenuType<DeployerMenu> DEPLOYER =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "deployer"), DeployerMenu::new);
    public static final MenuType<BufferMenu> BUFFER =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "buffer"), BufferMenu::new);
    public static final MenuType<ItemDetectorMenu> ITEM_DETECTOR =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "item_detector"), ItemDetectorMenu::new);
    public static final MenuType<RelayMenu> RELAY =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "relay"), RelayMenu::new);
    public static final MenuType<RetrieverMenu> RETRIEVER =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "retriever"), RetrieverMenu::new);
    public static final MenuType<RegulatorMenu> REGULATOR =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "regulator"), RegulatorMenu::new);
    public static final MenuType<SortingMachineMenu> SORTING_MACHINE =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "sorting_machine"), SortingMachineMenu::new);
    public static final MenuType<AutoCraftingTableMenu> AUTO_CRAFTING_TABLE =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "auto_crafting_table"), AutoCraftingTableMenu::new);
    public static final MenuType<AutoCraftingTableMk2Menu> ACT_MK2 =
            ScreenHandlerRegistry.registerExtended(new ResourceLocation(RPTubes.MOD_ID, "act_mk2"), AutoCraftingTableMk2Menu::new);

    public static void init() {
    }

    private ModMenus() {
    }
}
