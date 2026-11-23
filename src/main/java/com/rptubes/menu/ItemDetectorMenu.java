package com.rptubes.menu;

import com.rptubes.blockentity.ItemDetectorBlockEntity;
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
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemDetectorMenu extends AbstractContainerMenu {
    private final ItemDetectorBlockEntity detector;
    private final ContainerLevelAccess access;

    //? if fabric && >=1.20.6 {
    /*public ItemDetectorMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, getDetector(playerInv, pos));
    *///?} else {
    public ItemDetectorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getDetector(playerInv, buf));
    //?}
    }

    public ItemDetectorMenu(int id, Inventory playerInv, ItemDetectorBlockEntity detector) {
        //? if forgelike {
        super(ModMenus.ITEM_DETECTOR.get(), id);
        //?} else {
        /*super(ModMenus.ITEM_DETECTOR, id);
        *///?}
        this.detector = detector;
        this.access = ContainerLevelAccess.create(detector.getLevel(), detector.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(detector, col + row * 3, 62 + col * 18, 17 + row * 18));
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
                return detector.mode;
            }

            @Override
            public void set(int value) {
                detector.mode = (byte) value;
            }
        });
    }

    public int getMode() {
        return this.detector.mode;
    }

    /** 0 = next mode, 1 = previous (OG GuiItemDetect left/right click). */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        int m = this.detector.mode;
        if (id == 0) {
            m = m >= 2 ? 0 : m + 1;
        } else if (id == 1) {
            m = m <= 0 ? 2 : m - 1;
        } else {
            return false;
        }
        this.detector.mode = (byte) m;
        this.detector.setChanged();
        return true;
    }

    //? if <1.20.1 {
    /*private static ItemDetectorBlockEntity getDetector(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.getLevel().getBlockEntity(pos);
    *///?} else if fabric && >=1.20.6 {
    /*private static ItemDetectorBlockEntity getDetector(Inventory inv, BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    *///?} else {
    private static ItemDetectorBlockEntity getDetector(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    //?}
        if (be instanceof ItemDetectorBlockEntity detector) {
            return detector;
        }
        throw new IllegalStateException("Item detector missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        //? if forgelike {
        return stillValid(this.access, player, ModBlocks.ITEM_DETECTOR.get());
        //?} else {
        /*return stillValid(this.access, player, ModBlocks.ITEM_DETECTOR);
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
