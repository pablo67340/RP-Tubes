package com.rptubes.menu;

import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class RetrieverMenu extends Container {
    private final RetrieverBlockEntity retriever;
    private final IWorldPosCallable access;

    public RetrieverMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getRetriever(playerInv, buf));
    }

    public RetrieverMenu(int id, PlayerInventory playerInv, RetrieverBlockEntity retriever) {
        super(ModMenus.RETRIEVER.get(), id);
        this.retriever = retriever;
        this.access = IWorldPosCallable.create(retriever.getLevel(), retriever.getBlockPos());

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

        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return retriever.color & 0xFF;
            }

            @Override
            public void set(int value) {
                retriever.color = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return retriever.mode;
            }

            @Override
            public void set(int value) {
                retriever.mode = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
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
    public boolean clickMenuButton(PlayerEntity player, int id) {
        switch (id) {
            case 0: {
                int c = this.retriever.color & 0xFF;
                this.retriever.color = (byte) (c >= 16 ? 0 : c + 1);
                break;
            }
            case 1: {
                int c = this.retriever.color & 0xFF;
                this.retriever.color = (byte) (c <= 0 ? 16 : c - 1);
                break;
            }
            case 2:
            case 3:
                this.retriever.mode = (byte) (this.retriever.mode == 0 ? 1 : 0);
                break;
            default:
                return false;
        }
        this.retriever.setChanged();
        return true;
    }

    private static RetrieverBlockEntity getRetriever(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof RetrieverBlockEntity) {
            RetrieverBlockEntity retriever = (RetrieverBlockEntity) be;
            return retriever;
        }
        throw new IllegalStateException("Retriever missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.RETRIEVER.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
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
