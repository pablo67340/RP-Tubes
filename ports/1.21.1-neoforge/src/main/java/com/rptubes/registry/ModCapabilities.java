package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class ModCapabilities {
    private ModCapabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ALLOY_FURNACE.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.AUTO_CRAFTING_TABLE.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ACT_MK2.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.EJECTOR.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.RELAY.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.REGULATOR.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.FILTER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.RETRIEVER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.DEPLOYER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BUFFER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FILTER.get(),
                (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.RETRIEVER.get(),
                (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SORTING_MACHINE.get(),
                (be, side) -> be.getEnergyStorage());
    }
}
