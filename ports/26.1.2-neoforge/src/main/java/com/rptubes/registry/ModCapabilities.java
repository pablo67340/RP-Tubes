package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

@EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class ModCapabilities {
    private ModCapabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ALLOY_FURNACE.get(),
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
    }

    /**
     * 26.1 replaced {@code Capabilities.ItemHandler} with {@code Capabilities.Item}
     * ({@code ResourceHandler<ItemResource>}). Machine code still speaks IItemHandler.
     */
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
    }
}
