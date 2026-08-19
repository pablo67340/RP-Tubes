package com.rptubes.menu;

import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeHooks;

public class AlloyFurnaceMenu extends Container {
    private final IInventory container;
    private final IIntArray data;
    private final IWorldPosCallable access;

    public AlloyFurnaceMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getFurnace(playerInv, buf), new IntArray(4));
    }

    public AlloyFurnaceMenu(int id, PlayerInventory playerInv, AlloyFurnaceBlockEntity furnace, IIntArray data) {
        super(ModMenus.ALLOY_FURNACE.get(), id);
        this.container = furnace;
        this.data = data;
        this.access = IWorldPosCallable.create(furnace.getLevel(), furnace.getBlockPos());
        checkContainerSize(furnace, AlloyFurnaceBlockEntity.SIZE);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(furnace, col + row * 3, 48 + col * 18, 17 + row * 18));
            }
        }
        this.addSlot(new Slot(furnace, AlloyFurnaceBlockEntity.SLOT_FUEL, 17, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING) > 0;
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

    private static AlloyFurnaceBlockEntity getFurnace(PlayerInventory playerInv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = playerInv.player.level.getBlockEntity(pos);
        if (be instanceof AlloyFurnaceBlockEntity) {
            AlloyFurnaceBlockEntity furnace = (AlloyFurnaceBlockEntity) be;
            return furnace;
        }
        throw new IllegalStateException("Alloy furnace missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.ALLOY_FURNACE.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
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
                if (ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING) > 0) {
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
