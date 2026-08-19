package com.rptubes.menu;

import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IntReferenceHolder;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class SortingMachineMenu extends com.rptubes.util.SyncedContainer {
    private final SortingMachineBlockEntity sorter;

    public SortingMachineMenu(InventoryPlayer playerInv, SortingMachineBlockEntity sorter) {
        this.sorter = sorter;

        // OG ContainerSorter: 8 cols x 5 rows at (26 + 18*col, 18 + 18*row)
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                this.addSlotToContainer(new Slot(sorter, col + row * 8, 26 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 198));
        }

        for (int i = 0; i < 8; i++) {
            final int n = i;
            this.addDataSlot(new IntReferenceHolder() {
                @Override
                public int get() {
                    return sorter.colors[n];
                }

                @Override
                public void set(int value) {
                    sorter.colors[n] = (byte) value;
                }
            });
        }
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return sorter.column;
            }

            @Override
            public void set(int value) {
                sorter.column = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return sorter.mode;
            }

            @Override
            public void set(int value) {
                sorter.mode = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return sorter.defcolor;
            }

            @Override
            public void set(int value) {
                sorter.defcolor = (byte) value;
            }
        });
    }

    public int getColor(int column) {
        return this.sorter.colors[column] & 0xFF;
    }

    public int getColumn() {
        return this.sorter.column;
    }

    public int getMode() {
        return this.sorter.mode;
    }

    public int getDefColor() {
        return this.sorter.defcolor & 0xFF;
    }

    /**
     * 0/1 = mode next/prev, 10+n / 20+n = column n color next/prev,
     * 30/31 = default color next/prev (OG GuiSorter interactions).
     */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0 || id == 1) {
            int mode = this.sorter.mode + (id == 0 ? 1 : -1);
            if (mode > 4) {
                mode = 0;
            }
            if (mode < 0) {
                mode = 4;
            }
            this.sorter.mode = (byte) mode;
        } else if (id >= 10 && id < 18) {
            int n = id - 10;
            this.sorter.colors[n] = (byte) (this.sorter.colors[n] + 1 > 16 ? 0 : this.sorter.colors[n] + 1);
        } else if (id >= 20 && id < 28) {
            int n = id - 20;
            this.sorter.colors[n] = (byte) (this.sorter.colors[n] - 1 < 0 ? 16 : this.sorter.colors[n] - 1);
        } else if (id == 30) {
            this.sorter.defcolor = (byte) (this.sorter.defcolor + 1 > 16 ? 0 : this.sorter.defcolor + 1);
        } else if (id == 31) {
            this.sorter.defcolor = (byte) (this.sorter.defcolor - 1 < 0 ? 16 : this.sorter.defcolor - 1);
        } else {
            return false;
        }
        this.sorter.markDirty();
        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < 40) {
                if (!this.mergeItemStack(stack, 40, 76, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 0, 40, false)) {
                return null;
            }
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }
        return result;
    }
}
