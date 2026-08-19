package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID, value = Side.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        item(ModItems.BRASS_INGOT.get());
        item(ModItems.SCREWDRIVER.get());
        item(ModItems.TIN_INGOT.get());
        item(ModItems.TIN_ORE.get());
        item(ModItems.ALLOY_FURNACE.get());
        item(ModItems.TRANSPOSER.get());
        item(ModItems.FILTER.get());
        item(ModItems.EJECTOR.get());
        item(ModItems.BLOCK_BREAKER.get());
        item(ModItems.DEPLOYER.get());
        item(ModItems.BUFFER.get());
        item(ModItems.ITEM_DETECTOR.get());
        item(ModItems.RELAY.get());
        item(ModItems.RETRIEVER.get());
        item(ModItems.REGULATOR.get());
        item(ModItems.SORTING_MACHINE.get());
        item(ModItems.IGNITER.get());
        item(ModItems.AUTO_CRAFTING_TABLE.get());
        item(ModItems.ACT_MK2.get());
        item(ModItems.PNEUMATIC_TUBE.get());
        item(ModItems.REDSTONE_TUBE.get());
        item(ModItems.RESTRICTION_TUBE.get());

        ClientRegistry.bindTileEntitySpecialRenderer(PneumaticTubeBlockEntity.class, new TubeItemRenderer());
    }

    private static void item(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
