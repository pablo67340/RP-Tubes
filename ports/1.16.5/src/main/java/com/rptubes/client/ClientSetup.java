package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.tube.PaintColors;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
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

    @SubscribeEvent
    public static void onBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof PneumaticTubeBlockEntity) {
                return PaintColors.rgb(((PneumaticTubeBlockEntity) te).getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get());
    }

    @SubscribeEvent
    public static void onItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, tint) -> {
            if (stack.hasTag()) {
                return PaintColors.rgb(stack.getTag().getByte("pCol"));
            }
            return 0xFFFFFF;
        }, ModItems.PNEUMATIC_TUBE.get(), ModItems.REDSTONE_TUBE.get(), ModItems.RESTRICTION_TUBE.get());
    }
}
