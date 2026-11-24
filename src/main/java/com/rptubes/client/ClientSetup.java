package com.rptubes.client;

//? if fabric && <1.20.6 {
/*import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.PaintColors;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;

*///?} else if neoforge && <1.21.2 {
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

@EventBusSubscriber(modid = RPTubes.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//?} else if neoforge && <1.21.11 {
/*import com.rptubes.RPTubes;
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
*///?} else if fabric && >=1.21.11 && <26.1.2 {
/*import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.PaintColors;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import com.rptubes.registry.ModMenus;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

*///?} else if forge && >=1.21.11 && <26.1.2 {
/*import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import com.rptubes.tube.PaintColors;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
*///?} else if >=1.21.11 && <26.1.2 {
/*import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import com.rptubes.tube.PaintColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = RPTubes.MOD_ID, value = Dist.CLIENT)
*///?} else if <1.20.6 {
/*import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.tube.PaintColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
*///?} else if fabric && <1.21.11 {
/*import com.rptubes.registry.ModBlockEntities;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.PaintColors;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import com.rptubes.registry.ModMenus;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;

*///?} else if <1.21.11 {
/*import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.tube.PaintColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
*///?} else if fabric {
/*import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.PaintColors;
import com.rptubes.registry.ModMenus;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;

*///?} else if forge {
/*import com.rptubes.RPTubes;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
*///?} else {
/*import com.rptubes.RPTubes;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = RPTubes.MOD_ID, value = Dist.CLIENT)
*///?}
public final class ClientSetup {
    private ClientSetup() {
    }

    //? if fabric && <1.20.6 {
    /*public static void init() {
        MenuScreens.register(ModMenus.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        MenuScreens.register(ModMenus.FILTER, FilterScreen::new);
        MenuScreens.register(ModMenus.EJECTOR, EjectorScreen::new);
        MenuScreens.register(ModMenus.DEPLOYER, DeployerScreen::new);
        MenuScreens.register(ModMenus.BUFFER, BufferScreen::new);
        MenuScreens.register(ModMenus.ITEM_DETECTOR, ItemDetectorScreen::new);
        MenuScreens.register(ModMenus.RELAY, RelayScreen::new);
        MenuScreens.register(ModMenus.BATTERY_BOX, BatteryBoxScreen::new);
        MenuScreens.register(ModMenus.RETRIEVER, RetrieverScreen::new);
        MenuScreens.register(ModMenus.REGULATOR, RegulatorScreen::new);
        MenuScreens.register(ModMenus.RETRIEVULATOR, RetrievulatorScreen::new);
        MenuScreens.register(ModMenus.SORTING_MACHINE, SortingMachineScreen::new);
        MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE, AutoCraftingTableScreen::new);
        MenuScreens.register(ModMenus.ACT_MK2, AutoCraftingTableMk2Screen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PNEUMATIC_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REDSTONE_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RESTRICTION_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MAG_TUBE, RenderType.cutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.PNEUMATIC_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.REDSTONE_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.RESTRICTION_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MAG_TUBE, TubeItemRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (com.rptubes.tube.TubeAccess.connectable(te) instanceof PneumaticTubeBlockEntity tube) {
                return PaintColors.rgb(tube.getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE, ModBlocks.REDSTONE_TUBE, ModBlocks.RESTRICTION_TUBE, ModBlocks.MAG_TUBE);

        ColorProviderRegistry.ITEM.register((stack, tint) -> PaintColors.rgb(DyeHelper.readPaint(stack)),
                ModItems.PNEUMATIC_TUBE, ModItems.REDSTONE_TUBE, ModItems.RESTRICTION_TUBE, ModItems.MAG_TUBE);
    }
    *///?} else if fabric && >=1.21.11 && <26.1.2 {
    /*public static void init() {
        MenuScreens.register(ModMenus.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        MenuScreens.register(ModMenus.FILTER, FilterScreen::new);
        MenuScreens.register(ModMenus.EJECTOR, EjectorScreen::new);
        MenuScreens.register(ModMenus.DEPLOYER, DeployerScreen::new);
        MenuScreens.register(ModMenus.BUFFER, BufferScreen::new);
        MenuScreens.register(ModMenus.ITEM_DETECTOR, ItemDetectorScreen::new);
        MenuScreens.register(ModMenus.RELAY, RelayScreen::new);
        MenuScreens.register(ModMenus.BATTERY_BOX, BatteryBoxScreen::new);
        MenuScreens.register(ModMenus.RETRIEVER, RetrieverScreen::new);
        MenuScreens.register(ModMenus.REGULATOR, RegulatorScreen::new);
        MenuScreens.register(ModMenus.RETRIEVULATOR, RetrievulatorScreen::new);
        MenuScreens.register(ModMenus.SORTING_MACHINE, SortingMachineScreen::new);
        MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE, AutoCraftingTableScreen::new);
        MenuScreens.register(ModMenus.ACT_MK2, AutoCraftingTableMk2Screen::new);

        // Tube cutout render type comes from "render_type" in the block model JSONs

        BlockEntityRendererRegistry.register(ModBlockEntities.PNEUMATIC_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.REDSTONE_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.RESTRICTION_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MAG_TUBE, TubeItemRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            net.minecraft.world.level.block.entity.BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof PneumaticTubeBlockEntity) {
                return PaintColors.rgb(((PneumaticTubeBlockEntity) te).getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE, ModBlocks.REDSTONE_TUBE, ModBlocks.RESTRICTION_TUBE, ModBlocks.MAG_TUBE);

        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.rptubes.RPTubes.MOD_ID, "tube_paint"),
                TubePaintTintSource.MAP_CODEC);
    }
    *///?} else if forge && >=1.21.11 && <26.1.2 {
    /*@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
            MenuScreens.register(ModMenus.FILTER.get(), FilterScreen::new);
            MenuScreens.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
            MenuScreens.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
            MenuScreens.register(ModMenus.BUFFER.get(), BufferScreen::new);
            MenuScreens.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
            MenuScreens.register(ModMenus.RELAY.get(), RelayScreen::new);
            MenuScreens.register(ModMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
            MenuScreens.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
            MenuScreens.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
            MenuScreens.register(ModMenus.RETRIEVULATOR.get(), RetrievulatorScreen::new);
            MenuScreens.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
            MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
            MenuScreens.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
            // Tube cutout render type now comes from "render_type" in the block model JSONs
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAG_TUBE.get(), TubeItemRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (com.rptubes.tube.TubeAccess.connectable(te) instanceof PneumaticTubeBlockEntity tube) {
                return PaintColors.rgb(tube.getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get(), ModBlocks.MAG_TUBE.get());
        // Forge has no item tint source event; this fires before item models load.
        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_paint"),
                TubePaintTintSource.MAP_CODEC);
    }
    *///?} else if >=1.21.11 && <26.1.2 {
    /*@SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
        event.register(ModMenus.FILTER.get(), FilterScreen::new);
        event.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
        event.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
        event.register(ModMenus.BUFFER.get(), BufferScreen::new);
        event.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
        event.register(ModMenus.RELAY.get(), RelayScreen::new);
        event.register(ModMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
        event.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
        event.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
        event.register(ModMenus.RETRIEVULATOR.get(), RetrievulatorScreen::new);
        event.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
        event.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
        event.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAG_TUBE.get(), TubeItemRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (com.rptubes.tube.TubeAccess.connectable(te) instanceof PneumaticTubeBlockEntity tube) {
                return PaintColors.rgb(tube.getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get(), ModBlocks.MAG_TUBE.get());
    }

    @SubscribeEvent
    public static void onItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "paint"), TubePaintTintSource.MAP_CODEC);
    }
    *///?} else if <1.20.6 {
    /*@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
            MenuScreens.register(ModMenus.FILTER.get(), FilterScreen::new);
            MenuScreens.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
            MenuScreens.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
            MenuScreens.register(ModMenus.BUFFER.get(), BufferScreen::new);
            MenuScreens.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
            MenuScreens.register(ModMenus.RELAY.get(), RelayScreen::new);
            MenuScreens.register(ModMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
            MenuScreens.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
            MenuScreens.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
            MenuScreens.register(ModMenus.RETRIEVULATOR.get(), RetrievulatorScreen::new);
            MenuScreens.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
            MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
            MenuScreens.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.PNEUMATIC_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REDSTONE_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.RESTRICTION_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAG_TUBE.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAG_TUBE.get(), TubeItemRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (com.rptubes.tube.TubeAccess.connectable(te) instanceof PneumaticTubeBlockEntity tube) {
                return PaintColors.rgb(tube.getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get(), ModBlocks.MAG_TUBE.get());
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> {
            if (stack.hasTag()) {
                return PaintColors.rgb(stack.getTag().getByte("pCol"));
            }
            return 0xFFFFFF;
        }, ModItems.PNEUMATIC_TUBE.get(), ModItems.REDSTONE_TUBE.get(), ModItems.RESTRICTION_TUBE.get(), ModItems.MAG_TUBE.get());
    }
    *///?} else if fabric && <1.21.11 {
    /*public static void init() {
        MenuScreens.register(ModMenus.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        MenuScreens.register(ModMenus.FILTER, FilterScreen::new);
        MenuScreens.register(ModMenus.EJECTOR, EjectorScreen::new);
        MenuScreens.register(ModMenus.DEPLOYER, DeployerScreen::new);
        MenuScreens.register(ModMenus.BUFFER, BufferScreen::new);
        MenuScreens.register(ModMenus.ITEM_DETECTOR, ItemDetectorScreen::new);
        MenuScreens.register(ModMenus.RELAY, RelayScreen::new);
        MenuScreens.register(ModMenus.BATTERY_BOX, BatteryBoxScreen::new);
        MenuScreens.register(ModMenus.RETRIEVER, RetrieverScreen::new);
        MenuScreens.register(ModMenus.REGULATOR, RegulatorScreen::new);
        MenuScreens.register(ModMenus.RETRIEVULATOR, RetrievulatorScreen::new);
        MenuScreens.register(ModMenus.SORTING_MACHINE, SortingMachineScreen::new);
        MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE, AutoCraftingTableScreen::new);
        MenuScreens.register(ModMenus.ACT_MK2, AutoCraftingTableMk2Screen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PNEUMATIC_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REDSTONE_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RESTRICTION_TUBE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MAG_TUBE, RenderType.cutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.PNEUMATIC_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.REDSTONE_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.RESTRICTION_TUBE, TubeItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MAG_TUBE, TubeItemRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            net.minecraft.world.level.block.entity.BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof PneumaticTubeBlockEntity) {
                return PaintColors.rgb(((PneumaticTubeBlockEntity) te).getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE, ModBlocks.REDSTONE_TUBE, ModBlocks.RESTRICTION_TUBE, ModBlocks.MAG_TUBE);

        ColorProviderRegistry.ITEM.register((stack, tint) -> PaintColors.rgb(DyeHelper.readPaint(stack)),
                ModItems.PNEUMATIC_TUBE, ModItems.REDSTONE_TUBE, ModItems.RESTRICTION_TUBE, ModItems.MAG_TUBE);
    }
    *///?} else if forge && <1.21.11 {
    /*@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
            MenuScreens.register(ModMenus.FILTER.get(), FilterScreen::new);
            MenuScreens.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
            MenuScreens.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
            MenuScreens.register(ModMenus.BUFFER.get(), BufferScreen::new);
            MenuScreens.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
            MenuScreens.register(ModMenus.RELAY.get(), RelayScreen::new);
            MenuScreens.register(ModMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
            MenuScreens.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
            MenuScreens.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
            MenuScreens.register(ModMenus.RETRIEVULATOR.get(), RetrievulatorScreen::new);
            MenuScreens.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
            MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
            MenuScreens.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.PNEUMATIC_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REDSTONE_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.RESTRICTION_TUBE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAG_TUBE.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAG_TUBE.get(), TubeItemRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (com.rptubes.tube.TubeAccess.connectable(te) instanceof PneumaticTubeBlockEntity tube) {
                return PaintColors.rgb(tube.getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get(), ModBlocks.MAG_TUBE.get());
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> {
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            return PaintColors.rgb(data.copyTag().getByte("pCol"));
        }, ModItems.PNEUMATIC_TUBE.get(), ModItems.REDSTONE_TUBE.get(), ModItems.RESTRICTION_TUBE.get(), ModItems.MAG_TUBE.get());
    }
    *///?} else if <1.21.11 {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
        event.register(ModMenus.FILTER.get(), FilterScreen::new);
        event.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
        event.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
        event.register(ModMenus.BUFFER.get(), BufferScreen::new);
        event.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
        event.register(ModMenus.RELAY.get(), RelayScreen::new);
        event.register(ModMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
        event.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
        event.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
        event.register(ModMenus.RETRIEVULATOR.get(), RetrievulatorScreen::new);
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
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAG_TUBE.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAG_TUBE.get(), TubeItemRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tint) -> {
            if (world == null || pos == null) {
                return 0xFFFFFF;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (com.rptubes.tube.TubeAccess.connectable(te) instanceof PneumaticTubeBlockEntity tube) {
                return PaintColors.rgb(tube.getPaintColor());
            }
            return 0xFFFFFF;
        }, ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get(), ModBlocks.MAG_TUBE.get());
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                return PaintColors.rgb(data.copyTag().getByte("pCol"));
            }
            return 0xFFFFFF;
        }, ModItems.PNEUMATIC_TUBE.get(), ModItems.REDSTONE_TUBE.get(), ModItems.RESTRICTION_TUBE.get(), ModItems.MAG_TUBE.get());
    }
    //?} else if fabric {
    /*public static void init() {
        MenuScreens.register(ModMenus.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        MenuScreens.register(ModMenus.FILTER, FilterScreen::new);
        MenuScreens.register(ModMenus.EJECTOR, EjectorScreen::new);
        MenuScreens.register(ModMenus.DEPLOYER, DeployerScreen::new);
        MenuScreens.register(ModMenus.BUFFER, BufferScreen::new);
        MenuScreens.register(ModMenus.ITEM_DETECTOR, ItemDetectorScreen::new);
        MenuScreens.register(ModMenus.RELAY, RelayScreen::new);
        MenuScreens.register(ModMenus.BATTERY_BOX, BatteryBoxScreen::new);
        MenuScreens.register(ModMenus.RETRIEVER, RetrieverScreen::new);
        MenuScreens.register(ModMenus.REGULATOR, RegulatorScreen::new);
        MenuScreens.register(ModMenus.RETRIEVULATOR, RetrievulatorScreen::new);
        MenuScreens.register(ModMenus.SORTING_MACHINE, SortingMachineScreen::new);
        MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE, AutoCraftingTableScreen::new);
        MenuScreens.register(ModMenus.ACT_MK2, AutoCraftingTableMk2Screen::new);

        BlockEntityRenderers.register(ModBlockEntities.PNEUMATIC_TUBE, TubeItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.REDSTONE_TUBE, TubeItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.RESTRICTION_TUBE, TubeItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.MAG_TUBE, TubeItemRenderer::new);

        BlockColorRegistry.register((state, level, pos, tintValues) -> {
            int rgb = 0xFFFFFF;
            if (level != null && pos != null) {
                BlockEntity te = level.getBlockEntity(pos);
                if (com.rptubes.tube.TubeAccess.connectable(te) instanceof PneumaticTubeBlockEntity tube) {
                    rgb = PaintColors.rgb(tube.getPaintColor());
                }
            }
            tintValues.add(0xFF000000 | rgb);
        }, ModBlocks.PNEUMATIC_TUBE, ModBlocks.REDSTONE_TUBE, ModBlocks.RESTRICTION_TUBE, ModBlocks.MAG_TUBE);

        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.rptubes.RPTubes.MOD_ID, "tube_paint"),
                TubePaintTintSource.MAP_CODEC);
    }
    *///?} else if forge {
    /*@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
            MenuScreens.register(ModMenus.FILTER.get(), FilterScreen::new);
            MenuScreens.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
            MenuScreens.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
            MenuScreens.register(ModMenus.BUFFER.get(), BufferScreen::new);
            MenuScreens.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
            MenuScreens.register(ModMenus.RELAY.get(), RelayScreen::new);
            MenuScreens.register(ModMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
            MenuScreens.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
            MenuScreens.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
            MenuScreens.register(ModMenus.RETRIEVULATOR.get(), RetrievulatorScreen::new);
            MenuScreens.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
            MenuScreens.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
            MenuScreens.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
            // Tube cutout render type now comes from "render_type" in the block model JSONs
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAG_TUBE.get(), TubeItemRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(java.util.List.of(TubePaintBlockTint.INSTANCE),
                ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get(), ModBlocks.MAG_TUBE.get());
        // Forge has no item tint source event; this fires before item models load.
        net.minecraft.client.color.item.ItemTintSources.ID_MAPPER.put(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_paint"),
                TubePaintTintSource.MAP_CODEC);
    }
    *///?} else {
    /*@SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ALLOY_FURNACE.get(), AlloyFurnaceScreen::new);
        event.register(ModMenus.FILTER.get(), FilterScreen::new);
        event.register(ModMenus.EJECTOR.get(), EjectorScreen::new);
        event.register(ModMenus.DEPLOYER.get(), DeployerScreen::new);
        event.register(ModMenus.BUFFER.get(), BufferScreen::new);
        event.register(ModMenus.ITEM_DETECTOR.get(), ItemDetectorScreen::new);
        event.register(ModMenus.RELAY.get(), RelayScreen::new);
        event.register(ModMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
        event.register(ModMenus.RETRIEVER.get(), RetrieverScreen::new);
        event.register(ModMenus.REGULATOR.get(), RegulatorScreen::new);
        event.register(ModMenus.RETRIEVULATOR.get(), RetrievulatorScreen::new);
        event.register(ModMenus.SORTING_MACHINE.get(), SortingMachineScreen::new);
        event.register(ModMenus.AUTO_CRAFTING_TABLE.get(), AutoCraftingTableScreen::new);
        event.register(ModMenus.ACT_MK2.get(), AutoCraftingTableMk2Screen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PNEUMATIC_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESTRICTION_TUBE.get(), TubeItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAG_TUBE.get(), TubeItemRenderer::new);
    }

    @SubscribeEvent
    public static void onBlockTintSources(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(TubePaintBlockTint.INSTANCE),
                ModBlocks.PNEUMATIC_TUBE.get(), ModBlocks.REDSTONE_TUBE.get(), ModBlocks.RESTRICTION_TUBE.get(), ModBlocks.MAG_TUBE.get());
    }

    @SubscribeEvent
    public static void onItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "paint"), TubePaintTintSource.MAP_CODEC);
    }
    *///?}
}
