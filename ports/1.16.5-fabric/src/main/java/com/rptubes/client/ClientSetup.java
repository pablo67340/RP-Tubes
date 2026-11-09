package com.rptubes.client;

import com.rptubes.registry.ModBlockEntities;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.PaintColors;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import com.rptubes.registry.ModMenus;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.renderer.RenderType;

public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init() {
        ScreenRegistry.register(ModMenus.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        ScreenRegistry.register(ModMenus.FILTER, FilterScreen::new);
        ScreenRegistry.register(ModMenus.EJECTOR, EjectorScreen::new);
        ScreenRegistry.register(ModMenus.DEPLOYER, DeployerScreen::new);
        ScreenRegistry.register(ModMenus.BUFFER, BufferScreen::new);
        ScreenRegistry.register(ModMenus.ITEM_DETECTOR, ItemDetectorScreen::new);
        ScreenRegistry.register(ModMenus.RELAY, RelayScreen::new);
        ScreenRegistry.register(ModMenus.RETRIEVER, RetrieverScreen::new);
        ScreenRegistry.register(ModMenus.REGULATOR, RegulatorScreen::new);
        ScreenRegistry.register(ModMenus.SORTING_MACHINE, SortingMachineScreen::new);
        ScreenRegistry.register(ModMenus.AUTO_CRAFTING_TABLE, AutoCraftingTableScreen::new);
        ScreenRegistry.register(ModMenus.ACT_MK2, AutoCraftingTableMk2Screen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PNEUMATIC_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REDSTONE_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RESTRICTION_TUBE, RenderType.cutout());

        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntities.PNEUMATIC_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntities.REDSTONE_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntities.RESTRICTION_TUBE, TubeItemRenderer::new);

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

        ColorProviderRegistry.ITEM.register((stack, tint) -> PaintColors.rgb(DyeHelper.readPaint(stack)),
                ModItems.PNEUMATIC_TUBE, ModItems.REDSTONE_TUBE, ModItems.RESTRICTION_TUBE);
    }
}
