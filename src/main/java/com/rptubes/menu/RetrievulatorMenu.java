package com.rptubes.menu;

import com.rptubes.blockentity.RetrievulatorBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
//? if forgelike || <1.20.6 || (>=1.21.1 && <1.21.11) {
import net.minecraft.network.FriendlyByteBuf;
//?}
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
//? if <26.1.2 {
import net.minecraft.world.inventory.ClickType;
//?} else {
/*import net.minecraft.world.inventory.ContainerInput;
*///?}
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.rptubes.blockentity.RetrievulatorBlockEntity.GRID;
import static com.rptubes.blockentity.RetrievulatorBlockEntity.MAX_TARGET;

/**
 * R grid (real stacks, the amount fetched per pulse) on the left, T grid (ghost
 * targets) on the right. On a T square, right click adds one, left click removes
 * one, shift steps by ten, and clicking with a different stack sets it.
 */
public class RetrievulatorMenu extends AbstractContainerMenu {
    // Menu slot indices: 0-8 R grid, 9-17 T grid, 18-53 player
    private static final int TARGET_START = GRID;
    private static final int PLAYER_START = GRID * 2;
    private static final int PLAYER_END = PLAYER_START + 36;

    private final RetrievulatorBlockEntity retrievulator;
    private final ContainerLevelAccess access;

    //? if fabric && >=1.20.6 {
    /*public RetrievulatorMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, getRetrievulator(playerInv, pos));
    *///?} else {
    public RetrievulatorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getRetrievulator(playerInv, buf));
    //?}
    }

    public RetrievulatorMenu(int id, Inventory playerInv, RetrievulatorBlockEntity retrievulator) {
        //? if forgelike {
        super(ModMenus.RETRIEVULATOR.get(), id);
        //?} else {
        /*super(ModMenus.RETRIEVULATOR, id);
        *///?}
        this.retrievulator = retrievulator;
        this.access = ContainerLevelAccess.create(retrievulator.getLevel(), retrievulator.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(retrievulator, col + row * 3, 26 + col * 18, 18 + row * 18));
            }
        }
        Container targets = retrievulator.getTargetView();
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

    //? if <1.20.1 {
    /*private static RetrievulatorBlockEntity getRetrievulator(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.getLevel().getBlockEntity(pos);
    *///?} else if fabric && >=1.20.6 {
    /*private static RetrievulatorBlockEntity getRetrievulator(Inventory inv, BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    *///?} else {
    private static RetrievulatorBlockEntity getRetrievulator(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    //?}
        if (be instanceof RetrievulatorBlockEntity retrievulator) {
            return retrievulator;
        }
        throw new IllegalStateException("Retrievulator missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        //? if forgelike {
        return stillValid(this.access, player, ModBlocks.RETRIEVULATOR.get());
        //?} else {
        /*return stillValid(this.access, player, ModBlocks.RETRIEVULATOR);
        *///?}
    }

    @Override
    //? if <26.1.2 {
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        boolean pickup = clickType == ClickType.PICKUP;
        boolean shift = clickType == ClickType.QUICK_MOVE;
    //?} else {
    /*public void clicked(int slotId, int button, ContainerInput clickType, Player player) {
        boolean pickup = clickType == ContainerInput.PICKUP;
        boolean shift = clickType == ContainerInput.QUICK_MOVE;
    *///?}
        if (slotId >= TARGET_START && slotId < PLAYER_START && (pickup || shift)) {
            int t = slotId - TARGET_START;
            ItemStack carried = this.getCarried();
            ItemStack target = this.retrievulator.getTarget(t);
            int count = this.retrievulator.getTargetCount(t);
            if (!carried.isEmpty() && (target.isEmpty() || !sameItem(carried, target))) {
                this.retrievulator.setTarget(t, carried, carried.getCount());
            } else if (!target.isEmpty()) {
                int step = shift ? 10 : 1;
                if (button == 1) {
                    this.retrievulator.setTarget(t, target, Math.min(MAX_TARGET, count + step));
                } else if (button == 0) {
                    this.retrievulator.setTarget(t, target, count - step);
                }
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    private static boolean sameItem(ItemStack a, ItemStack b) {
        //? if <1.20.6 {
        /*return ItemStack.isSameItemSameTags(a, b);
        *///?} else {
        return ItemStack.isSameItemSameComponents(a, b);
        //?}
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
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
        GhostSlot(Container targets, int slot, int x, int y) {
            super(targets, slot, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
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
