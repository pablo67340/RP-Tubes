package com.rptubes.menu;

import com.rptubes.blockentity.BufferBlockEntity;
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

public class BufferMenu extends Container {
    private final IWorldPosCallable access;

    public BufferMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getBuffer(playerInv, buf));
    }

    public BufferMenu(int id, PlayerInventory playerInv, BufferBlockEntity buffer) {
        super(ModMenus.BUFFER.get(), id);
        this.access = IWorldPosCallable.create(buffer.getLevel(), buffer.getBlockPos());

        // OG ContainerBufferChest: 5 columns x 4 rows, column-major slot indices
        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 4; row++) {
                this.addSlot(new Slot(buffer, row + col * 4, 44 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 162));
        }
    }

    private static BufferBlockEntity getBuffer(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof BufferBlockEntity) {
            BufferBlockEntity buffer = (BufferBlockEntity) be;
            return buffer;
        }
        throw new IllegalStateException("Buffer missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.BUFFER.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 20) {
                if (!this.moveItemStackTo(stack, 20, 56, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 20, false)) {
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
