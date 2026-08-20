package com.rptubes.inventory;

import net.minecraft.core.Direction;

/**
 * Block entities that expose a sided item handler to tubes and hoppers.
 */
public interface ItemHandlerProvider {
    IItemHandler getItemHandler(Direction side);
}
