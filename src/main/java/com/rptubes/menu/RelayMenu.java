package com.rptubes.menu;

import com.rptubes.blockentity.RelayBlockEntity;
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

public class RelayMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    //? if fabric && >=1.20.6 {
    /*public RelayMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, getRelay(playerInv, pos));
    *///?} else {
    public RelayMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getRelay(playerInv, buf));
    //?}
    }

    public RelayMenu(int id, Inventory playerInv, RelayBlockEntity relay) {
        //? if forgelike {
        super(ModMenus.RELAY.get(), id);
        //?} else {
        /*super(ModMenus.RELAY, id);
        *///?}
        this.access = ContainerLevelAccess.create(relay.getLevel(), relay.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(relay, col + row * 3, 62 + col * 18, 17 + row * 18));
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

    //? if <1.20.1 {
    /*private static RelayBlockEntity getRelay(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.getLevel().getBlockEntity(pos);
    *///?} else if fabric && >=1.20.6 {
    /*private static RelayBlockEntity getRelay(Inventory inv, BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    *///?} else {
    private static RelayBlockEntity getRelay(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    //?}
        if (be instanceof RelayBlockEntity relay) {
            return relay;
        }
        throw new IllegalStateException("Relay missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        //? if forgelike {
        return stillValid(this.access, player, ModBlocks.RELAY.get());
        //?} else {
        /*return stillValid(this.access, player, ModBlocks.RELAY);
        *///?}
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
