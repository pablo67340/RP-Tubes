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
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
    public static final MenuType<AlloyFurnaceMenu> ALLOY_FURNACE =
            register("alloy_furnace", new ExtendedScreenHandlerType<>(AlloyFurnaceMenu::new, BlockPos.STREAM_CODEC));
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

    private ModMenus() {
    }
}
