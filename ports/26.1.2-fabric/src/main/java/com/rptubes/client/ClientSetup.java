package com.rptubes.client;

import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init() {
        MenuScreens.register(ModMenus.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        MenuScreens.register(ModMenus.FILTER, FilterScreen::new);
        MenuScreens.register(ModMenus.EJECTOR, EjectorScreen::new);
        MenuScreens.register(ModMenus.DEPLOYER, DeployerScreen::new);
        MenuScreens.register(ModMenus.BUFFER, BufferScreen::new);
        MenuScreens.register(ModMenus.ITEM_DETECTOR, ItemDetectorScreen::new);
        MenuScreens.register(ModMenus.RELAY, RelayScreen::new);
        MenuScreens.register(ModMenus.RETRIEVER, RetrieverScreen::new);
        MenuScreens.register(ModMenus.REGULATOR, RegulatorScreen::new);
        MenuScreens.register(ModMenus.SORTING_MACHINE, SortingMachineScreen::new);
        MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE, AutoCraftingTableScreen::new);
        MenuScreens.register(ModMenus.ACT_MK2, AutoCraftingTableMk2Screen::new);

        BlockEntityRenderers.register(ModBlockEntities.PNEUMATIC_TUBE, TubeItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.REDSTONE_TUBE, TubeItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.RESTRICTION_TUBE, TubeItemRenderer::new);
    }
}
