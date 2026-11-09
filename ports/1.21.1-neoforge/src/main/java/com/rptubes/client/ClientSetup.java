package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.tube.PaintColors;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
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
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
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

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof PneumaticTubeBlockEntity tube) {
                return PaintColors.rgb(tube.getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get());
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                return PaintColors.rgb(data.copyTag().getByte("pCol"));
            }
            return 0xFFFFFF;
        }, ModItems.PNEUMATIC_TUBE.get(), ModItems.REDSTONE_TUBE.get(), ModItems.RESTRICTION_TUBE.get());
    }
}
