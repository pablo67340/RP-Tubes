package com.rptubes.registry;

import com.rptubes.RPTubes;
//? if neoforge && <1.21.11 {
import net.minecraft.core.registries.Registries;
//?}
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.menu.BatteryBoxMenu;
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
//? if forge {
/*import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?} else if <1.20.6 {
/*import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
*///?} else if fabric && <26.1.2 {
/*import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
*///?} else if <1.21.11 {
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} else if neoforge {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?} else {
/*import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
*///?}

public final class ModMenus {
    //? if fabric && <1.20.6 {
    /*public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE =
            register("alloy_furnace", new ExtendedScreenHandlerType<>(AlloyFurnaceMenu::new));
    public static final MenuType<BatteryBoxMenu> BATTERY_BOX =
            register("battery_box", new ExtendedScreenHandlerType<>(BatteryBoxMenu::new));
    public static final MenuType<FilterMenu> FILTER =
            register("filter", new ExtendedScreenHandlerType<>(FilterMenu::new));
    public static final MenuType<EjectorMenu> EJECTOR =
            register("ejector", new ExtendedScreenHandlerType<>(EjectorMenu::new));
    public static final MenuType<DeployerMenu> DEPLOYER =
            register("deployer", new ExtendedScreenHandlerType<>(DeployerMenu::new));
    public static final MenuType<BufferMenu> BUFFER =
            register("buffer", new ExtendedScreenHandlerType<>(BufferMenu::new));
    public static final MenuType<ItemDetectorMenu> ITEM_DETECTOR =
            register("item_detector", new ExtendedScreenHandlerType<>(ItemDetectorMenu::new));
    public static final MenuType<RelayMenu> RELAY =
            register("relay", new ExtendedScreenHandlerType<>(RelayMenu::new));
    public static final MenuType<RetrieverMenu> RETRIEVER =
            register("retriever", new ExtendedScreenHandlerType<>(RetrieverMenu::new));
    public static final MenuType<RegulatorMenu> REGULATOR =
            register("regulator", new ExtendedScreenHandlerType<>(RegulatorMenu::new));
    public static final MenuType<SortingMachineMenu> SORTING_MACHINE =
            register("sorting_machine", new ExtendedScreenHandlerType<>(SortingMachineMenu::new));
    public static final MenuType<AutoCraftingTableMenu> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", new ExtendedScreenHandlerType<>(AutoCraftingTableMenu::new));
    public static final MenuType<AutoCraftingTableMk2Menu> ACT_MK2 =
            register("act_mk2", new ExtendedScreenHandlerType<>(AutoCraftingTableMk2Menu::new));

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
            String name, MenuType<T> type) {
        return Registry.register(BuiltInRegistries.MENU, new ResourceLocation(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
    *///?} else if fabric && <1.21.1 {
    /*public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE =
            register("alloy_furnace", new ExtendedScreenHandlerType<>(AlloyFurnaceMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BatteryBoxMenu> BATTERY_BOX =
            register("battery_box", new ExtendedScreenHandlerType<>(BatteryBoxMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<FilterMenu> FILTER =
            register("filter", new ExtendedScreenHandlerType<>(FilterMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<EjectorMenu> EJECTOR =
            register("ejector", new ExtendedScreenHandlerType<>(EjectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<DeployerMenu> DEPLOYER =
            register("deployer", new ExtendedScreenHandlerType<>(DeployerMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BufferMenu> BUFFER =
            register("buffer", new ExtendedScreenHandlerType<>(BufferMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<ItemDetectorMenu> ITEM_DETECTOR =
            register("item_detector", new ExtendedScreenHandlerType<>(ItemDetectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RelayMenu> RELAY =
            register("relay", new ExtendedScreenHandlerType<>(RelayMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RetrieverMenu> RETRIEVER =
            register("retriever", new ExtendedScreenHandlerType<>(RetrieverMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RegulatorMenu> REGULATOR =
            register("regulator", new ExtendedScreenHandlerType<>(RegulatorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<SortingMachineMenu> SORTING_MACHINE =
            register("sorting_machine", new ExtendedScreenHandlerType<>(SortingMachineMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMenu> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", new ExtendedScreenHandlerType<>(AutoCraftingTableMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMk2Menu> ACT_MK2 =
            register("act_mk2", new ExtendedScreenHandlerType<>(AutoCraftingTableMk2Menu::new, BlockPos.STREAM_CODEC));

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
            String name, MenuType<T> type) {
        return Registry.register(BuiltInRegistries.MENU, new ResourceLocation(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
    *///?} else if fabric && <1.21.11 {
    /*public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE =
            register("alloy_furnace", new ExtendedScreenHandlerType<>(AlloyFurnaceMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BatteryBoxMenu> BATTERY_BOX =
            register("battery_box", new ExtendedScreenHandlerType<>(BatteryBoxMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<FilterMenu> FILTER =
            register("filter", new ExtendedScreenHandlerType<>(FilterMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<EjectorMenu> EJECTOR =
            register("ejector", new ExtendedScreenHandlerType<>(EjectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<DeployerMenu> DEPLOYER =
            register("deployer", new ExtendedScreenHandlerType<>(DeployerMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BufferMenu> BUFFER =
            register("buffer", new ExtendedScreenHandlerType<>(BufferMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<ItemDetectorMenu> ITEM_DETECTOR =
            register("item_detector", new ExtendedScreenHandlerType<>(ItemDetectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RelayMenu> RELAY =
            register("relay", new ExtendedScreenHandlerType<>(RelayMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RetrieverMenu> RETRIEVER =
            register("retriever", new ExtendedScreenHandlerType<>(RetrieverMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RegulatorMenu> REGULATOR =
            register("regulator", new ExtendedScreenHandlerType<>(RegulatorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<SortingMachineMenu> SORTING_MACHINE =
            register("sorting_machine", new ExtendedScreenHandlerType<>(SortingMachineMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMenu> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", new ExtendedScreenHandlerType<>(AutoCraftingTableMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMk2Menu> ACT_MK2 =
            register("act_mk2", new ExtendedScreenHandlerType<>(AutoCraftingTableMk2Menu::new, BlockPos.STREAM_CODEC));

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
            String name, MenuType<T> type) {
        return Registry.register(BuiltInRegistries.MENU,
                ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
    *///?} else if fabric && <26.1.2 {
    /*public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE =
            register("alloy_furnace", new ExtendedScreenHandlerType<>(AlloyFurnaceMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BatteryBoxMenu> BATTERY_BOX =
            register("battery_box", new ExtendedScreenHandlerType<>(BatteryBoxMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<FilterMenu> FILTER =
            register("filter", new ExtendedScreenHandlerType<>(FilterMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<EjectorMenu> EJECTOR =
            register("ejector", new ExtendedScreenHandlerType<>(EjectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<DeployerMenu> DEPLOYER =
            register("deployer", new ExtendedScreenHandlerType<>(DeployerMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BufferMenu> BUFFER =
            register("buffer", new ExtendedScreenHandlerType<>(BufferMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<ItemDetectorMenu> ITEM_DETECTOR =
            register("item_detector", new ExtendedScreenHandlerType<>(ItemDetectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RelayMenu> RELAY =
            register("relay", new ExtendedScreenHandlerType<>(RelayMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RetrieverMenu> RETRIEVER =
            register("retriever", new ExtendedScreenHandlerType<>(RetrieverMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RegulatorMenu> REGULATOR =
            register("regulator", new ExtendedScreenHandlerType<>(RegulatorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<SortingMachineMenu> SORTING_MACHINE =
            register("sorting_machine", new ExtendedScreenHandlerType<>(SortingMachineMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMenu> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", new ExtendedScreenHandlerType<>(AutoCraftingTableMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMk2Menu> ACT_MK2 =
            register("act_mk2", new ExtendedScreenHandlerType<>(AutoCraftingTableMk2Menu::new, BlockPos.STREAM_CODEC));

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
            String name, MenuType<T> type) {
        return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
    *///?} else if fabric {
    /*public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE =
            register("alloy_furnace", new ExtendedMenuType<>(AlloyFurnaceMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BatteryBoxMenu> BATTERY_BOX =
            register("battery_box", new ExtendedMenuType<>(BatteryBoxMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<FilterMenu> FILTER =
            register("filter", new ExtendedMenuType<>(FilterMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<EjectorMenu> EJECTOR =
            register("ejector", new ExtendedMenuType<>(EjectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<DeployerMenu> DEPLOYER =
            register("deployer", new ExtendedMenuType<>(DeployerMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<BufferMenu> BUFFER =
            register("buffer", new ExtendedMenuType<>(BufferMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<ItemDetectorMenu> ITEM_DETECTOR =
            register("item_detector", new ExtendedMenuType<>(ItemDetectorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RelayMenu> RELAY =
            register("relay", new ExtendedMenuType<>(RelayMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RetrieverMenu> RETRIEVER =
            register("retriever", new ExtendedMenuType<>(RetrieverMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<RegulatorMenu> REGULATOR =
            register("regulator", new ExtendedMenuType<>(RegulatorMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<SortingMachineMenu> SORTING_MACHINE =
            register("sorting_machine", new ExtendedMenuType<>(SortingMachineMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMenu> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", new ExtendedMenuType<>(AutoCraftingTableMenu::new, BlockPos.STREAM_CODEC));
    public static final MenuType<AutoCraftingTableMk2Menu> ACT_MK2 =
            register("act_mk2", new ExtendedMenuType<>(AutoCraftingTableMk2Menu::new, BlockPos.STREAM_CODEC));

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
            String name, MenuType<T> type) {
        return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
    *///?} else if neoforge {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, RPTubes.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AlloyFurnaceMenu>> ALLOY_FURNACE =
            MENUS.register("alloy_furnace", () -> IMenuTypeExtension.create(AlloyFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<BatteryBoxMenu>> BATTERY_BOX =
            MENUS.register("battery_box", () -> IMenuTypeExtension.create(BatteryBoxMenu::new));
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
    //?} else {
    /*public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RPTubes.MOD_ID);

    public static final RegistryObject<MenuType<AlloyFurnaceMenu>> ALLOY_FURNACE =
            MENUS.register("alloy_furnace", () -> IForgeMenuType.create(AlloyFurnaceMenu::new));
    public static final RegistryObject<MenuType<BatteryBoxMenu>> BATTERY_BOX =
            MENUS.register("battery_box", () -> IForgeMenuType.create(BatteryBoxMenu::new));
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
    *///?}

    private ModMenus() {
    }
}
