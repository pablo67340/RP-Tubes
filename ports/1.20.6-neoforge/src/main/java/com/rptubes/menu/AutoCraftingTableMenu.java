package com.rptubes.menu;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Port of BuildCraft ContainerAutoWorkbench: output slot at 124,35, real-item
 * 3x3 grid at 30,17 — the vanilla crafting table layout. Taking the output
 * crafts from the grid (removeRecipe=true, like the original SlotAutoCrafting).
 */
public class AutoCraftingTableMenu extends AbstractContainerMenu {
    private final AutoCraftingTableBlockEntity table;
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;

    public AutoCraftingTableMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getTable(playerInv, buf));
    }

    public AutoCraftingTableMenu(int id, Inventory playerInv, AutoCraftingTableBlockEntity table) {
        super(ModMenus.AUTO_CRAFTING_TABLE.get(), id);
        this.table = table;
        this.access = ContainerLevelAccess.create(table.getLevel(), table.getBlockPos());

        this.addSlot(new ResultSlot(this.result, 0, 124, 35));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(table, col + row * 3, 30 + col * 18, 17 + row * 18));
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
        this.refreshResult();
    }

    private static AutoCraftingTableBlockEntity getTable(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof AutoCraftingTableBlockEntity table) {
            return table;
        }
        throw new IllegalStateException("Automatic Crafting Table missing at " + pos);
    }

    private void refreshResult() {
        this.result.setItem(0, this.table.findRecipe());
    }

    @Override
    public void broadcastChanges() {
        // The grid can change from automation or neighbors at any time,
        // so keep the preview current every sync tick (like OG detectAndSendChanges).
        this.refreshResult();
        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.AUTO_CRAFTING_TABLE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, result);
            } else if (index < 10) {
                if (!this.moveItemStackTo(stack, 10, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 1, 10, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return result;
    }

    /** Port of SlotAutoCrafting — read-only preview whose take performs the craft. */
    private class ResultSlot extends Slot {
        ResultSlot(ResultContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return !AutoCraftingTableMenu.this.table.findRecipe().isEmpty();
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            // GUI crafting consumes grid items outright (OG extractItem(true, true))
            AutoCraftingTableMenu.this.table.craftOne(true, true);
            AutoCraftingTableMenu.this.refreshResult();
            super.onTake(player, stack);
        }
    }
}
