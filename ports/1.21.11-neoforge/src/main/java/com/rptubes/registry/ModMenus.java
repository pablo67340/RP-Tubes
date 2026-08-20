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
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, RPTubes.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AlloyFurnaceMenu>> ALLOY_FURNACE =
            MENUS.register("alloy_furnace", () -> IMenuTypeExtension.create(AlloyFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<FilterMenu>> FILTER =
            MENUS.register("filter", () -> IMenuTypeExtension.create(FilterMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<EjectorMenu>> EJECTOR =
            MENUS.register("ejector", () -> IMenuTypeExtension.create(EjectorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<DeployerMenu>> DEPLOYER =
            MENUS.register("deployer", () -> IMenuTypeExtension.create(DeployerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BufferMenu>> BUFFER =
            MENUS.register("buffer", () -> IMenuTypeExtension.create(BufferMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<ItemDetectorMenu>> ITEM_DETECTOR =
            MENUS.register("item_detector", () -> IMenuTypeExtension.create(ItemDetectorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RelayMenu>> RELAY =
            MENUS.register("relay", () -> IMenuTypeExtension.create(RelayMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RetrieverMenu>> RETRIEVER =
            MENUS.register("retriever", () -> IMenuTypeExtension.create(RetrieverMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RegulatorMenu>> REGULATOR =
            MENUS.register("regulator", () -> IMenuTypeExtension.create(RegulatorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<SortingMachineMenu>> SORTING_MACHINE =
            MENUS.register("sorting_machine", () -> IMenuTypeExtension.create(SortingMachineMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<AutoCraftingTableMenu>> AUTO_CRAFTING_TABLE =
            MENUS.register("auto_crafting_table", () -> IMenuTypeExtension.create(AutoCraftingTableMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<AutoCraftingTableMk2Menu>> ACT_MK2 =
            MENUS.register("act_mk2", () -> IMenuTypeExtension.create(AutoCraftingTableMk2Menu::new));

    private ModMenus() {
    }
}
