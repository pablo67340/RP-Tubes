package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ScreenManager.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
            ScreenManager.register(ModMenus.FILTER.get(), FilterScreen::new);
            ScreenManager.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
            ScreenManager.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
            ScreenManager.register(ModMenus.BUFFER.get(), BufferScreen::new);
            ScreenManager.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
            ScreenManager.register(ModMenus.RELAY.get(), RelayScreen::new);
            ScreenManager.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
            ScreenManager.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
            ScreenManager.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
            ScreenManager.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
            ScreenManager.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
            RenderTypeLookup.setRenderLayer(ModBlocks.PNEUMATIC_TUBE.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.REDSTONE_TUBE.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.RESTRICTION_TUBE.get(), RenderType.cutout());

            ClientRegistry.bindTileEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
            ClientRegistry.bindTileEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
            ClientRegistry.bindTileEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        });
    }
}
