package com.rptubes.menu;

import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.registry.ModBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IntReferenceHolder;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class RegulatorMenu extends com.rptubes.util.SyncedContainer {
    private final RegulatorBlockEntity regulator;

    public RegulatorMenu(InventoryPlayer playerInv, RegulatorBlockEntity regulator) {
        this.regulator = regulator;

        // OG ContainerRegulator: three 3x3 grids — input template, buffer, output template
        for (int grid = 0; grid < 3; grid++) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    this.addSlotToContainer(new Slot(regulator, col + row * 3 + grid * 9,
                            8 + col * 18 + grid * 72, 18 + row * 18));
                }
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 26 + col * 18, 86 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 26 + col * 18, 144));
        }

        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return regulator.color & 0xFF;
            }

            @Override
            public void set(int value) {
                regulator.color = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return regulator.mode;
            }

            @Override
            public void set(int value) {
                regulator.mode = (byte) value;
            }
        });
    }

    public int getColor() {
        return this.regulator.color & 0xFF;
    }

    public int getMode() {
        return this.regulator.mode;
    }

    /** 0/1 = color next/prev (OG GuiRegulator), 2/3 = mode toggle. */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        switch (id) {
            case 0: {
                int c = this.regulator.color & 0xFF;
                this.regulator.color = (byte) (c >= 16 ? 0 : c + 1);
                break;
            }
            case 1: {
                int c = this.regulator.color & 0xFF;
                this.regulator.color = (byte) (c <= 0 ? 16 : c - 1);
                break;
            }
            case 2:
            case 3:
                this.regulator.mode = (byte) (this.regulator.mode == 0 ? 1 : 0);
                break;
            default:
                return false;
        }
        this.regulator.markDirty();
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
            if (index < 27) {
                if (!this.mergeItemStack(stack, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 9, 18, false)) {
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
