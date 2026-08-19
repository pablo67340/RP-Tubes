package com.rptubes.menu;

import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.util.BlockPos;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import com.rptubes.util.IIntArray;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeHooks;

public class AlloyFurnaceMenu extends com.rptubes.util.SyncedContainer {
    private final IInventory container;
    private final IIntArray data;

    public AlloyFurnaceMenu(InventoryPlayer playerInv, AlloyFurnaceBlockEntity furnace) {
        this.container = furnace;
        this.data = furnace.getData();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(furnace, col + row * 3, 48 + col * 18, 17 + row * 18));
            }
        }
        this.addSlotToContainer(new Slot(furnace, AlloyFurnaceBlockEntity.SLOT_FUEL, 17, 42) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime(stack) > 0;
            }
        });
        this.addSlotToContainer(new Slot(furnace, AlloyFurnaceBlockEntity.SLOT_OUTPUT, 141, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.addDataSlots(this.data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.container.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index == AlloyFurnaceBlockEntity.SLOT_OUTPUT) {
                if (!this.mergeItemStack(stack, 11, 47, true)) {
                    return null;
                }
            } else if (index >= 11) {
                if (net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime(stack) > 0) {
                    if (!this.mergeItemStack(stack, AlloyFurnaceBlockEntity.SLOT_FUEL,
                            AlloyFurnaceBlockEntity.SLOT_FUEL + 1, false)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(stack, 0, 9, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 11, 47, false)) {
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

    public int getBurnProgress() {
        int total = this.data.get(1);
        if (total == 0) {
            return 0;
        }
        // OG getBurnScaled(12)
        return this.data.get(0) * 12 / total;
    }

    public int getCookProgress() {
        int total = this.data.get(3);
        if (total == 0) {
            return 0;
        }
        return this.data.get(2) * 24 / total;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }
}
