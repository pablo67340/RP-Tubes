package com.rptubes.menu;

import com.rptubes.util.FuelHelper;

import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AlloyFurnaceMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public AlloyFurnaceMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, getFurnace(playerInv, pos), new SimpleContainerData(4));
    }

    public AlloyFurnaceMenu(int id, Inventory playerInv, AlloyFurnaceBlockEntity furnace, ContainerData data) {
        super(ModMenus.ALLOY_FURNACE, id);
        this.container = furnace;
        this.data = data;
        this.access = ContainerLevelAccess.create(furnace.getLevel(), furnace.getBlockPos());
        checkContainerSize(furnace, AlloyFurnaceBlockEntity.SIZE);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(furnace, col + row * 3, 48 + col * 18, 17 + row * 18));
            }
        }
        this.addSlot(new Slot(furnace, AlloyFurnaceBlockEntity.SLOT_FUEL, 17, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FuelHelper.getBurnTime(stack) > 0;
            }
        });
        this.addSlot(new Slot(furnace, AlloyFurnaceBlockEntity.SLOT_OUTPUT, 141, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    private static AlloyFurnaceBlockEntity getFurnace(Inventory playerInv, BlockPos pos) {
        BlockEntity be = playerInv.player.level().getBlockEntity(pos);
        if (be instanceof AlloyFurnaceBlockEntity furnace) {
            return furnace;
        }
        throw new IllegalStateException("Alloy furnace missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.ALLOY_FURNACE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index == AlloyFurnaceBlockEntity.SLOT_OUTPUT) {
                if (!this.moveItemStackTo(stack, 11, 47, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 11) {
                if (FuelHelper.getBurnTime(stack) > 0) {
                    if (!this.moveItemStackTo(stack, AlloyFurnaceBlockEntity.SLOT_FUEL,
                            AlloyFurnaceBlockEntity.SLOT_FUEL + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 0, 9, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 11, 47, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
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
