package com.rptubes.client;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.PaintColors;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import com.rptubes.registry.ModMenus;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

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

        // Tube cutout render type comes from "render_type" in the block model JSONs

        BlockEntityRendererRegistry.register(ModBlockEntities.PNEUMATIC_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.REDSTONE_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.RESTRICTION_TUBE, TubeItemRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            net.minecraft.world.level.block.entity.BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof PneumaticTubeBlockEntity) {
                return PaintColors.rgb(((PneumaticTubeBlockEntity) te).getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE, ModBlocks.REDSTONE_TUBE, ModBlocks.RESTRICTION_TUBE);

        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(com.rptubes.RPTubes.MOD_ID, "tube_paint"),
                TubePaintTintSource.MAP_CODEC);
    }
}
