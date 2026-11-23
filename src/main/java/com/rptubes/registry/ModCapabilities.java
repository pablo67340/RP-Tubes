//? if neoforge {
package com.rptubes.registry;

import com.rptubes.RPTubes;
//? if >=26.1.2 && <26.2 {
/*import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
*///?}
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//? if <1.21.1 {

/*@EventBusSubscriber(modid = RPTubes.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
*///?} else if <1.21.11 || >=26.2 {

@EventBusSubscriber(modid = RPTubes.MOD_ID)
//?} else if <26.1.2 {
/*import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

@EventBusSubscriber(modid = RPTubes.MOD_ID)
*///?} else {
/*import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

@EventBusSubscriber(modid = RPTubes.MOD_ID)
*///?}
public final class ModCapabilities {
    private ModCapabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        //? if <1.21.11 {
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
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SOLAR_PANEL.get(),
                (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.THERMOPILE.get(),
                (be, side) -> be.getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BATTERY_BOX.get(),
                (be, side) -> be.getEnergyStorage(side));
        //?} else if <26.1.2 {
        /*event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ALLOY_FURNACE.get(),
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
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SOLAR_PANEL.get(),
                (be, side) -> be.getEnergy(side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.THERMOPILE.get(),
                (be, side) -> be.getEnergy(side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.BATTERY_BOX.get(),
                (be, side) -> be.getEnergy(side));
        *///?} else if <26.2 {
        /*event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ALLOY_FURNACE.get(),
                (be, side) -> new WorldlyContainerWrapper(be, side));
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
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.AUTO_CRAFTING_TABLE.get(),
                (be, side) -> wrap(be.getItemHandler(side)));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ACT_MK2.get(),
                (be, side) -> wrap(be.getItemHandler(side)));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.FILTER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.RETRIEVER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SORTING_MACHINE.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SOLAR_PANEL.get(),
                (be, side) -> be.getEnergy(side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.THERMOPILE.get(),
                (be, side) -> be.getEnergy(side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.BATTERY_BOX.get(),
                (be, side) -> be.getEnergy(side));
    }

    /^*
     * 26.1 replaced {@code Capabilities.ItemHandler} with {@code Capabilities.Item}
     * ({@code ResourceHandler<ItemResource>}). Machine code still speaks IItemHandler.
     ^/
    public static IItemHandler query(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                                    Direction side) {
        ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, side);
        return handler == null ? null : IItemHandler.of(handler);
    }

    @SuppressWarnings("removal")
    private static ResourceHandler<ItemResource> wrap(IItemHandler handler) {
        if (handler == null) {
            return null;
        }
        return new ResourceHandler<>() {
            @Override
            public int size() {
                return handler.getSlots();
            }

            @Override
            public ItemResource getResource(int index) {
                return ItemResource.of(handler.getStackInSlot(index));
            }

            @Override
            public long getAmountAsLong(int index) {
                return handler.getStackInSlot(index).getCount();
            }

            @Override
            public long getCapacityAsLong(int index, ItemResource resource) {
                return handler.getSlotLimit(index);
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                return handler.isItemValid(index, resource.toStack());
            }

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
                ItemStack leftover = handler.insertItem(index, resource.toStack(amount), false);
                return amount - leftover.getCount();
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
                ItemStack extracted = handler.extractItem(index, amount, false);
                return extracted.getCount();
            }
        };
        *///?} else {
        /*event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ALLOY_FURNACE.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.AUTO_CRAFTING_TABLE.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ACT_MK2.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.EJECTOR.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.RELAY.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.REGULATOR.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.FILTER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.RETRIEVER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.DEPLOYER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.BUFFER.get(),
                (be, side) -> be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.FILTER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.RETRIEVER.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SORTING_MACHINE.get(),
                (be, side) -> be.getEnergy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.SOLAR_PANEL.get(),
                (be, side) -> be.getEnergy(side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.THERMOPILE.get(),
                (be, side) -> be.getEnergy(side));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlockEntities.BATTERY_BOX.get(),
                (be, side) -> be.getEnergy(side));
        *///?}
    }
}
//?}
