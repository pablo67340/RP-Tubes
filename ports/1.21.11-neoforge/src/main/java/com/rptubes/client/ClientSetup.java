package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = RPTubes.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
        event.register(ModMenus.FILTER.get(), FilterScreen::new);
        event.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
        event.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
        event.register(ModMenus.BUFFER.get(), BufferScreen::new);
        event.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
        event.register(ModMenus.RELAY.get(), RelayScreen::new);
        event.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
        event.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
        event.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
        event.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
        event.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
    }
}
