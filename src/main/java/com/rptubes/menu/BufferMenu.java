package com.rptubes.menu;

import com.rptubes.blockentity.BufferBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
//? if forgelike || <1.20.6 || (>=1.21.1 && <1.21.11) {
import net.minecraft.network.FriendlyByteBuf;
//?}
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BufferMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    //? if fabric && >=1.20.6 {
    /*public BufferMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, getBuffer(playerInv, pos));
    *///?} else {
    public BufferMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getBuffer(playerInv, buf));
    //?}
    }

    public BufferMenu(int id, Inventory playerInv, BufferBlockEntity buffer) {
        //? if forgelike {
        super(ModMenus.BUFFER.get(), id);
        //?} else {
        /*super(ModMenus.BUFFER, id);
        *///?}
        this.access = ContainerLevelAccess.create(buffer.getLevel(), buffer.getBlockPos());

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

    //? if <1.20.1 {
    /*private static BufferBlockEntity getBuffer(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.getLevel().getBlockEntity(pos);
    *///?} else if fabric && >=1.20.6 {
    /*private static BufferBlockEntity getBuffer(Inventory inv, BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    *///?} else {
    private static BufferBlockEntity getBuffer(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    //?}
        if (be instanceof BufferBlockEntity buffer) {
            return buffer;
        }
        throw new IllegalStateException("Buffer missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        //? if forgelike {
        return stillValid(this.access, player, ModBlocks.BUFFER.get());
        //?} else {
        /*return stillValid(this.access, player, ModBlocks.BUFFER);
        *///?}
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
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
