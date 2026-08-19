package com.rptubes.menu;

import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RetrieverMenu extends AbstractContainerMenu {
    private final RetrieverBlockEntity retriever;
    private final ContainerLevelAccess access;

    public RetrieverMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getRetriever(playerInv, buf));
    }

    public RetrieverMenu(int id, Inventory playerInv, RetrieverBlockEntity retriever) {
        super(ModMenus.RETRIEVER.get(), id);
        this.retriever = retriever;
        this.access = ContainerLevelAccess.create(retriever.getLevel(), retriever.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(retriever, col + row * 3, 62 + col * 18, 17 + row * 18));
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

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return retriever.color & 0xFF;
            }

            @Override
            public void set(int value) {
                retriever.color = (byte) value;
            }
        });
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return retriever.mode;
            }

            @Override
            public void set(int value) {
                retriever.mode = (byte) value;
            }
        });
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return retriever.select;
            }

            @Override
            public void set(int value) {
                retriever.select = (byte) value;
            }
        });
    }

    public int getColor() {
        return this.retriever.color & 0xFF;
    }

    public int getMode() {
        return this.retriever.mode;
    }

    public int getSelect() {
        return this.retriever.select;
    }

    /** 0/1 = color next/prev (OG GuiRetriever), 2/3 = mode toggle. */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 -> {
                int c = this.retriever.color & 0xFF;
                this.retriever.color = (byte) (c >= 16 ? 0 : c + 1);
            }
            case 1 -> {
                int c = this.retriever.color & 0xFF;
                this.retriever.color = (byte) (c <= 0 ? 16 : c - 1);
            }
            case 2, 3 -> this.retriever.mode = (byte) (this.retriever.mode == 0 ? 1 : 0);
            default -> {
                return false;
            }
        }
        this.retriever.setChanged();
        return true;
    }

    private static RetrieverBlockEntity getRetriever(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity retriever) {
            return retriever;
        }
        throw new IllegalStateException("Retriever missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.RETRIEVER.get());
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
