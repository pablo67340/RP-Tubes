package com.rptubes.menu;

import com.rptubes.blockentity.EjectorBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class EjectorMenu extends Container {
    private final IWorldPosCallable access;

    public EjectorMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getEjector(playerInv, buf));
    }

    public EjectorMenu(int id, PlayerInventory playerInv, EjectorBlockEntity ejector) {
        super(ModMenus.EJECTOR.get(), id);
        this.access = IWorldPosCallable.create(ejector.getLevel(), ejector.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(ejector, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    private static EjectorBlockEntity getEjector(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof EjectorBlockEntity) {
            EjectorBlockEntity ejector = (EjectorBlockEntity) be;
            return ejector;
        }
        throw new IllegalStateException("Ejector missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.EJECTOR.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 9) {
                if (!this.moveItemStackTo(stack, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 9, false)) {
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
}
