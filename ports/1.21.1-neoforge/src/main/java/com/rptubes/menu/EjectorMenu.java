package com.rptubes.menu;

import com.rptubes.blockentity.EjectorBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EjectorMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public EjectorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getEjector(playerInv, buf));
    }

    public EjectorMenu(int id, Inventory playerInv, EjectorBlockEntity ejector) {
        super(ModMenus.EJECTOR.get(), id);
        this.access = ContainerLevelAccess.create(ejector.getLevel(), ejector.getBlockPos());

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

    private static EjectorBlockEntity getEjector(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof EjectorBlockEntity ejector) {
            return ejector;
        }
        throw new IllegalStateException("Ejector missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.EJECTOR.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
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
