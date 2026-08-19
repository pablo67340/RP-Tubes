package com.rptubes.menu;

import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import static com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity.SIZE_INPUT;
import static com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity.SLOT_OUTPUT;
import static com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity.START_INPUT;
import static com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity.START_RECIPE;

/**
 * Port of Tubestuff's ContainerCraftingTable. Original coordinates:
 * output 134,124; ghost recipe 3x3 at 31,107; five 9-slot input rows at
 * 13,6 with 19px vertical spacing; player inventory at 13,168 / 13,226.
 * The recipe grid uses fake slots — clicking paints a template copy.
 */
public class AutoCraftingTableMk2Menu extends Container {
    // Menu slot indices: 0 output, 1-9 ghost recipe, 10-54 input, 55-90 player
    private static final int PLAYER_START = 1 + 9 + SIZE_INPUT;
    private static final int PLAYER_END = PLAYER_START + 36;

    private final AutoCraftingTableMk2BlockEntity table;
    private final IWorldPosCallable access;

    // Ghost-template fingerprint so the GUI preview only re-resolves on change
    private final NonNullList<ItemStack> previewGhost = NonNullList.withSize(9, ItemStack.EMPTY);
    private ItemStack previewResult = ItemStack.EMPTY;

    public AutoCraftingTableMk2Menu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getTable(playerInv, buf));
    }

    public AutoCraftingTableMk2Menu(int id, PlayerInventory playerInv, AutoCraftingTableMk2BlockEntity table) {
        super(ModMenus.ACT_MK2.get(), id);
        this.table = table;
        this.access = IWorldPosCallable.create(table.getLevel(), table.getBlockPos());

        this.addSlot(new OutputSlot(table, SLOT_OUTPUT, 134, 124));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new GhostSlot(table, x + y * 3 + START_RECIPE, 31 + x * 18, 107 + y * 18));
            }
        }
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(table, x + y * 9 + START_INPUT, 13 + x * 18, 6 + y * 19));
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 13 + x * 18, 168 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInv, x, 13 + x * 18, 226));
        }
    }

    private static AutoCraftingTableMk2BlockEntity getTable(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof AutoCraftingTableMk2BlockEntity) {
            AutoCraftingTableMk2BlockEntity table = (AutoCraftingTableMk2BlockEntity) be;
            return table;
        }
        throw new IllegalStateException("ACT Mk II missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.ACT_MK2.get());
    }

    /** Client-side: what the ghost template would craft (for the faded preview). */
    public ItemStack getResultPreview() {
        boolean dirty = false;
        for (int k = 0; k < 9; k++) {
            ItemStack ghost = this.table.getItem(k + START_RECIPE);
            if (!ItemStack.matches(ghost, this.previewGhost.get(k))) {
                this.previewGhost.set(k, ghost.copy());
                dirty = true;
            }
        }
        if (dirty) {
            this.previewResult = this.table.findRecipe();
        }
        return this.previewResult;
    }

    @Override
    public ItemStack clicked(int slotId, int button, ClickType clickType, PlayerEntity player) {
        // Ghost recipe slots: paint a single-item template copy, never move real items
        if (slotId >= 1 && slotId < 1 + 9) {
            Slot slot = this.slots.get(slotId);
            ItemStack carried = player.inventory.getCarried();
            if (carried.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                ItemStack ghost = carried.copy();
                ghost.setCount(1);
                slot.set(ghost);
            }
            return carried;
        }
        return super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        // Ghost slots never shift-transfer
        if (index >= 1 && index < 10) {
            return ItemStack.EMPTY;
        }
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < PLAYER_START) {
                if (!this.moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 10, 10 + SIZE_INPUT, false)) {
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

    private static class OutputSlot extends Slot {
        OutputSlot(AutoCraftingTableMk2BlockEntity table, int slot, int x, int y) {
            super(table, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static class GhostSlot extends Slot {
        GhostSlot(AutoCraftingTableMk2BlockEntity table, int slot, int x, int y) {
            super(table, slot, x, y);
        }

        @Override
        public boolean mayPickup(PlayerEntity player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
