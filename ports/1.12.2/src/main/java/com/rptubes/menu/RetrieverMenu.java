package com.rptubes.menu;

import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.registry.ModBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IntReferenceHolder;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class RetrieverMenu extends com.rptubes.util.SyncedContainer {
    private final RetrieverBlockEntity retriever;

    public RetrieverMenu(InventoryPlayer playerInv, RetrieverBlockEntity retriever) {
        this.retriever = retriever;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(retriever, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return retriever.color & 0xFF;
            }

            @Override
            public void set(int value) {
                retriever.color = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return retriever.mode;
            }

            @Override
            public void set(int value) {
                retriever.mode = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return retriever.select;
            }

            @Override
            public void set(int value) {
                retriever.select = (byte) value;
            }
        });
    }

    public int getColor() {
        return this.retriever.color & 0xFF;
    }

    public int getMode() {
        return this.retriever.mode;
    }

    public int getSelect() {
        return this.retriever.select;
    }

    /** 0/1 = color next/prev (OG GuiRetriever), 2/3 = mode toggle. */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        switch (id) {
            case 0: {
                int c = this.retriever.color & 0xFF;
                this.retriever.color = (byte) (c >= 16 ? 0 : c + 1);
                break;
            }
            case 1: {
                int c = this.retriever.color & 0xFF;
                this.retriever.color = (byte) (c <= 0 ? 16 : c - 1);
                break;
            }
            case 2:
            case 3:
                this.retriever.mode = (byte) (this.retriever.mode == 0 ? 1 : 0);
                break;
            default:
                return false;
        }
        this.retriever.markDirty();
        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < 9) {
                if (!this.mergeItemStack(stack, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return result;
    }
}
