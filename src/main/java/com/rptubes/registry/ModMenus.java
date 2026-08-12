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
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RPTubes.MOD_ID);

    public static final RegistryObject<MenuType<AlloyFurnaceMenu>> ALLOY_FURNACE =
            MENUS.register("alloy_furnace", () -> IForgeMenuType.create(AlloyFurnaceMenu::new));
    public static final RegistryObject<MenuType<FilterMenu>> FILTER =
            MENUS.register("filter", () -> IForgeMenuType.create(FilterMenu::new));
    public static final RegistryObject<MenuType<EjectorMenu>> EJECTOR =
            MENUS.register("ejector", () -> IForgeMenuType.create(EjectorMenu::new));
    public static final RegistryObject<MenuType<DeployerMenu>> DEPLOYER =
            MENUS.register("deployer", () -> IForgeMenuType.create(DeployerMenu::new));
    public static final RegistryObject<MenuType<BufferMenu>> BUFFER =
            MENUS.register("buffer", () -> IForgeMenuType.create(BufferMenu::new));
    public static final RegistryObject<MenuType<ItemDetectorMenu>> ITEM_DETECTOR =
            MENUS.register("item_detector", () -> IForgeMenuType.create(ItemDetectorMenu::new));
    public static final RegistryObject<MenuType<RelayMenu>> RELAY =
            MENUS.register("relay", () -> IForgeMenuType.create(RelayMenu::new));
    public static final RegistryObject<MenuType<RetrieverMenu>> RETRIEVER =
            MENUS.register("retriever", () -> IForgeMenuType.create(RetrieverMenu::new));
    public static final RegistryObject<MenuType<RegulatorMenu>> REGULATOR =
            MENUS.register("regulator", () -> IForgeMenuType.create(RegulatorMenu::new));
    public static final RegistryObject<MenuType<SortingMachineMenu>> SORTING_MACHINE =
            MENUS.register("sorting_machine", () -> IForgeMenuType.create(SortingMachineMenu::new));
    public static final RegistryObject<MenuType<AutoCraftingTableMenu>> AUTO_CRAFTING_TABLE =
            MENUS.register("auto_crafting_table", () -> IForgeMenuType.create(AutoCraftingTableMenu::new));
    public static final RegistryObject<MenuType<AutoCraftingTableMk2Menu>> ACT_MK2 =
            MENUS.register("act_mk2", () -> IForgeMenuType.create(AutoCraftingTableMk2Menu::new));

    private ModMenus() {
    }
}
