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
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<ContainerType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, RPTubes.MOD_ID);

    public static final RegistryObject<ContainerType<AlloyFurnaceMenu>> ALLOY_FURNACE =
            MENUS.register("alloy_furnace", () -> IForgeContainerType.create(AlloyFurnaceMenu::new));
    public static final RegistryObject<ContainerType<FilterMenu>> FILTER =
            MENUS.register("filter", () -> IForgeContainerType.create(FilterMenu::new));
    public static final RegistryObject<ContainerType<EjectorMenu>> EJECTOR =
            MENUS.register("ejector", () -> IForgeContainerType.create(EjectorMenu::new));
    public static final RegistryObject<ContainerType<DeployerMenu>> DEPLOYER =
            MENUS.register("deployer", () -> IForgeContainerType.create(DeployerMenu::new));
    public static final RegistryObject<ContainerType<BufferMenu>> BUFFER =
            MENUS.register("buffer", () -> IForgeContainerType.create(BufferMenu::new));
    public static final RegistryObject<ContainerType<ItemDetectorMenu>> ITEM_DETECTOR =
            MENUS.register("item_detector", () -> IForgeContainerType.create(ItemDetectorMenu::new));
    public static final RegistryObject<ContainerType<RelayMenu>> RELAY =
            MENUS.register("relay", () -> IForgeContainerType.create(RelayMenu::new));
    public static final RegistryObject<ContainerType<RetrieverMenu>> RETRIEVER =
            MENUS.register("retriever", () -> IForgeContainerType.create(RetrieverMenu::new));
    public static final RegistryObject<ContainerType<RegulatorMenu>> REGULATOR =
            MENUS.register("regulator", () -> IForgeContainerType.create(RegulatorMenu::new));
    public static final RegistryObject<ContainerType<SortingMachineMenu>> SORTING_MACHINE =
            MENUS.register("sorting_machine", () -> IForgeContainerType.create(SortingMachineMenu::new));
    public static final RegistryObject<ContainerType<AutoCraftingTableMenu>> AUTO_CRAFTING_TABLE =
            MENUS.register("auto_crafting_table", () -> IForgeContainerType.create(AutoCraftingTableMenu::new));
    public static final RegistryObject<ContainerType<AutoCraftingTableMk2Menu>> ACT_MK2 =
            MENUS.register("act_mk2", () -> IForgeContainerType.create(AutoCraftingTableMk2Menu::new));

    private ModMenus() {
    }
}
