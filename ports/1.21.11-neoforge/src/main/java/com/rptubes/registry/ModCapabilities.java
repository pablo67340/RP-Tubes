package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

@EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class ModCapabilities {
    private ModCapabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ALLOY_FURNACE.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.AUTO_CRAFTING_TABLE.get(),
                (be, side) -> be.getResourceHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ACT_MK2.get(),
                (be, side) -> be.getResourceHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.EJECTOR.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.RELAY.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.REGULATOR.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.FILTER.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.RETRIEVER.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.DEPLOYER.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.BUFFER.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.FILTER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.RETRIEVER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SORTING_MACHINE.get(),
                (be, side) -> be.getEnergy());
    }
}
