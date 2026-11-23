package com.rptubes.menu;

import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.power.PowerBuffer;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.core.BlockPos;
//? if forgelike || <1.20.6 {
import net.minecraft.network.FriendlyByteBuf;
//?}
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/** OG ContainerBatteryBox: charge slot at (120, 27), drain slot at (120, 55), 170 px tall. */
public class BatteryBoxMenu extends AbstractContainerMenu {
    public static final int MAX_STORAGE = (int) (BatteryBoxBlockEntity.CAPACITY / PowerBuffer.FE_PER_UNIT);
    private final ContainerData data;
    private final ContainerLevelAccess access;

    //? if fabric && >=1.20.6 {
    /*public BatteryBoxMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, getBox(playerInv, pos), new SimpleContainerData(3));
    *///?} else {
    public BatteryBoxMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getBox(playerInv, buf), new SimpleContainerData(3));
    //?}
    }

    public BatteryBoxMenu(int id, Inventory playerInv, BatteryBoxBlockEntity box, ContainerData data) {
        //? if forgelike {
        super(ModMenus.BATTERY_BOX.get(), id);
        //?} else {
        /*super(ModMenus.BATTERY_BOX, id);
        *///?}
        this.data = data;
        this.access = ContainerLevelAccess.create(box.getLevel(), box.getBlockPos());
        checkContainerSize(box, 2);

        this.addSlot(new Slot(box, BatteryBoxBlockEntity.SLOT_CHARGE, 120, 27));
        this.addSlot(new Slot(box, BatteryBoxBlockEntity.SLOT_DRAIN, 120, 55));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 88 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 146));
        }
        this.addDataSlots(data);
    }

    //? if <1.20.1 {
    /*private static BatteryBoxBlockEntity getBox(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.getLevel().getBlockEntity(pos);
    *///?} else if fabric && >=1.20.6 {
    /*private static BatteryBoxBlockEntity getBox(Inventory inv, BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    *///?} else {
    private static BatteryBoxBlockEntity getBox(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    //?}
        if (be instanceof BatteryBoxBlockEntity box) {
            return box;
        }
        throw new IllegalStateException("Battery Box missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        //? if forgelike {
        return stillValid(this.access, player, ModBlocks.BATTERY_BOX.get());
        //?} else {
        /*return stillValid(this.access, player, ModBlocks.BATTERY_BOX);
        *///?}
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 2, false)) {
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

    /** Stored energy in RP2 units, 0 to {@link #MAX_STORAGE}. */
    public int getStorage() {
        return this.data.get(0);
    }

    public int getInRate() {
        return this.data.get(1);
    }

    public int getOutRate() {
        return this.data.get(2);
    }
}
