package com.rptubes.menu;

import com.rptubes.blockentity.RetrievulatorBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

import static com.rptubes.blockentity.RetrievulatorBlockEntity.GRID;
import static com.rptubes.blockentity.RetrievulatorBlockEntity.MAX_TARGET;

/**
 * R grid (real stacks, the amount fetched per pulse) on the left, T grid (ghost
 * targets) on the right. On a T square, right click adds one, left click removes
 * one, shift steps by ten, and clicking with a different stack sets it.
 */
public class RetrievulatorMenu extends Container {
    // Menu slot indices: 0-8 R grid, 9-17 T grid, 18-53 player
    private static final int TARGET_START = GRID;
    private static final int PLAYER_START = GRID * 2;
    private static final int PLAYER_END = PLAYER_START + 36;

    private final RetrievulatorBlockEntity retrievulator;
    private final IWorldPosCallable access;

    public RetrievulatorMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getRetrievulator(playerInv, buf));
    }

    public RetrievulatorMenu(int id, PlayerInventory playerInv, RetrievulatorBlockEntity retrievulator) {
        super(ModMenus.RETRIEVULATOR.get(), id);
        this.retrievulator = retrievulator;
        this.access = IWorldPosCallable.create(retrievulator.getLevel(), retrievulator.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(retrievulator, col + row * 3, 26 + col * 18, 18 + row * 18));
            }
        }
        IInventory targets = retrievulator.getTargetView();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new GhostSlot(targets, col + row * 3, 98 + col * 18, 18 + row * 18));
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

    private static RetrievulatorBlockEntity getRetrievulator(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof RetrievulatorBlockEntity) {
            return (RetrievulatorBlockEntity) be;
        }
        throw new IllegalStateException("Retrievulator missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.RETRIEVULATOR.get());
    }

    @Override
    public ItemStack clicked(int slotId, int button, ClickType clickType, PlayerEntity player) {
        boolean pickup = clickType == ClickType.PICKUP;
        boolean shift = clickType == ClickType.QUICK_MOVE;
        if (slotId >= TARGET_START && slotId < PLAYER_START && (pickup || shift)) {
            int t = slotId - TARGET_START;
            ItemStack carried = player.inventory.getCarried();
            ItemStack target = this.retrievulator.getTarget(t);
            int count = this.retrievulator.getTargetCount(t);
            if (!carried.isEmpty() && (target.isEmpty() || !RetrievulatorBlockEntity.sameItem(carried, target))) {
                this.retrievulator.setTarget(t, carried, carried.getCount());
            } else if (!target.isEmpty()) {
                int step = shift ? 10 : 1;
                if (button == 1) {
                    this.retrievulator.setTarget(t, target, Math.min(MAX_TARGET, count + step));
                } else if (button == 0) {
                    this.retrievulator.setTarget(t, target, count - step);
                }
            }
            return ItemStack.EMPTY;
        }
        return super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        if (index >= TARGET_START && index < PLAYER_START) {
            return ItemStack.EMPTY;
        }
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < TARGET_START) {
                if (!this.moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, TARGET_START, false)) {
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

    private static class GhostSlot extends Slot {
        GhostSlot(IInventory targets, int slot, int x, int y) {
            super(targets, slot, x, y);
        }

        @Override
        public boolean mayPickup(PlayerEntity player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public int getMaxStackSize() {
            return MAX_TARGET;
        }
    }
}
