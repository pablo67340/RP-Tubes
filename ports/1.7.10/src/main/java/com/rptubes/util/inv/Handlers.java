package com.rptubes.util.inv;

import com.rptubes.util.Stacks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Replaces getCapability(ITEM_HANDLER_CAPABILITY, side) lookups on 1.7.10:
 * tiles may provide a custom handler via IItemHandlerProvider, otherwise any
 * IInventory (sided or not) is wrapped on the fly.
 */
public final class Handlers {

    public interface IItemHandlerProvider {
        IItemHandler getItemHandler(ForgeDirection side);
    }

    public static IItemHandler wrap(TileEntity be, ForgeDirection side) {
        if (be instanceof IItemHandlerProvider) {
            return ((IItemHandlerProvider) be).getItemHandler(side);
        }
        if (be instanceof ISidedInventory) {
            return new SidedInvWrapper((ISidedInventory) be, side);
        }
        if (be instanceof IInventory) {
            return new InvWrapper((IInventory) be);
        }
        return null;
    }

    /** ItemHandlerHelper.insertItem: distribute into existing stacks first, then empties. */
    public static ItemStack insertItem(IItemHandler handler, ItemStack stack, boolean simulate) {
        if (handler == null || Stacks.isEmpty(stack)) {
            return stack;
        }
        // pass 1: top up matching stacks
        for (int i = 0; i < handler.getSlots() && !Stacks.isEmpty(stack); i++) {
            ItemStack existing = handler.getStackInSlot(i);
            if (!Stacks.isEmpty(existing) && existing.isItemEqual(stack)
                    && ItemStack.areItemStackTagsEqual(existing, stack)) {
                stack = handler.insertItem(i, stack, simulate);
            }
        }
        // pass 2: fill empty slots
        for (int i = 0; i < handler.getSlots() && !Stacks.isEmpty(stack); i++) {
            if (Stacks.isEmpty(handler.getStackInSlot(i))) {
                stack = handler.insertItem(i, stack, simulate);
            }
        }
        return Stacks.orNull(stack);
    }

    private Handlers() {
    }
}
