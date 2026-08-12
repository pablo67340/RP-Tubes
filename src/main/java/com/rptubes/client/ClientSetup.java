package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
            MenuScreens.register(ModMenus.FILTER.get(), FilterScreen::new);
            MenuScreens.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
            MenuScreens.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
            MenuScreens.register(ModMenus.BUFFER.get(), BufferScreen::new);
            MenuScreens.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
            MenuScreens.register(ModMenus.RELAY.get(), RelayScreen::new);
            MenuScreens.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
            MenuScreens.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
            MenuScreens.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
            MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
            MenuScreens.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.PNEUMATIC_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REDSTONE_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.RESTRICTION_TUBE.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
    }
}
