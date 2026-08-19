package com.rptubes.menu;

import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

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
public class AutoCraftingTableMk2Menu extends AbstractContainerMenu {
    // Menu slot indices: 0 output, 1-9 ghost recipe, 10-54 input, 55-90 player
    private static final int PLAYER_START = 1 + 9 + SIZE_INPUT;
    private static final int PLAYER_END = PLAYER_START + 36;

    private final AutoCraftingTableMk2BlockEntity table;
    private final ContainerLevelAccess access;

    // 1.21.2+ clients can't resolve recipes; the preview is computed
    // server-side and synced through a hidden off-screen slot.
    private final net.minecraft.world.inventory.ResultContainer previewContainer =
            new net.minecraft.world.inventory.ResultContainer();
    private final Slot previewSlot;

    // Ghost-template fingerprint so the preview only re-resolves on change
    private final NonNullList<ItemStack> previewGhost = NonNullList.withSize(9, ItemStack.EMPTY);

    public AutoCraftingTableMk2Menu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getTable(playerInv, buf));
    }

    public AutoCraftingTableMk2Menu(int id, Inventory playerInv, AutoCraftingTableMk2BlockEntity table) {
        super(ModMenus.ACT_MK2.get(), id);
        this.table = table;
        this.access = ContainerLevelAccess.create(table.getLevel(), table.getBlockPos());

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
        this.previewSlot = this.addSlot(new Slot(this.previewContainer, 0, -10000, -10000) {
            @Override
            public boolean mayPickup(Player player) {
                return false;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    private static AutoCraftingTableMk2BlockEntity getTable(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof AutoCraftingTableMk2BlockEntity table) {
            return table;
        }
        throw new IllegalStateException("ACT Mk II missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.ACT_MK2.get());
    }

    /** What the ghost template would craft (for the faded preview); synced from the server. */
    public ItemStack getResultPreview() {
        return this.previewSlot.getItem();
    }

    @Override
    public void broadcastChanges() {
        // Server-side: keep the synced preview current with the ghost template
        boolean dirty = false;
        for (int k = 0; k < 9; k++) {
            ItemStack ghost = this.table.getItem(k + START_RECIPE);
            if (!ItemStack.matches(ghost, this.previewGhost.get(k))) {
                this.previewGhost.set(k, ghost.copy());
                dirty = true;
            }
        }
        if (dirty) {
            this.previewContainer.setItem(0, this.table.findRecipe());
        }
        super.broadcastChanges();
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput clickType, Player player) {
        // Ghost recipe slots: paint a single-item template copy, never move real items
        if (slotId >= 1 && slotId < 1 + 9) {
            Slot slot = this.slots.get(slotId);
            ItemStack carried = this.getCarried();
            if (carried.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                ItemStack ghost = carried.copy();
                ghost.setCount(1);
                slot.set(ghost);
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
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
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
