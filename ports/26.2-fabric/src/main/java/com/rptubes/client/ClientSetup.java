package com.rptubes.client;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.PaintColors;
import com.rptubes.registry.ModMenus;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;

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

        BlockColorRegistry.register((state, level, pos, tintValues) -> {
            int rgb = 0xFFFFFF;
            if (level != null && pos != null) {
                BlockEntity te = level.getBlockEntity(pos);
                if (te instanceof PneumaticTubeBlockEntity tube) {
                    rgb = PaintColors.rgb(tube.getPaintColor());
                }
            }
            tintValues.add(0xFF000000 | rgb);
        }, ModBlocks.PNEUMATIC_TUBE, ModBlocks.REDSTONE_TUBE, ModBlocks.RESTRICTION_TUBE);

        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(com.rptubes.RPTubes.MOD_ID, "tube_paint"),
                TubePaintTintSource.MAP_CODEC);
    }
}
