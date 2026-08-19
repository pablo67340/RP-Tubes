package com.rptubes.menu;

import com.rptubes.blockentity.RegulatorBlockEntity;
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

public class RegulatorMenu extends Container {
    private final RegulatorBlockEntity regulator;
    private final IWorldPosCallable access;

    public RegulatorMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getRegulator(playerInv, buf));
    }

    public RegulatorMenu(int id, PlayerInventory playerInv, RegulatorBlockEntity regulator) {
        super(ModMenus.REGULATOR.get(), id);
        this.regulator = regulator;
        this.access = IWorldPosCallable.create(regulator.getLevel(), regulator.getBlockPos());

        // OG ContainerRegulator: three 3x3 grids — input template, buffer, output template
        for (int grid = 0; grid < 3; grid++) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    this.addSlot(new Slot(regulator, col + row * 3 + grid * 9,
                            8 + col * 18 + grid * 72, 18 + row * 18));
                }
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 26 + col * 18, 86 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 26 + col * 18, 144));
        }

        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return regulator.color & 0xFF;
            }

            @Override
            public void set(int value) {
                regulator.color = (byte) value;
            }
        });
        this.addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return regulator.mode;
            }

            @Override
            public void set(int value) {
                regulator.mode = (byte) value;
            }
        });
    }

    public int getColor() {
        return this.regulator.color & 0xFF;
    }

    public int getMode() {
        return this.regulator.mode;
    }

    /** 0/1 = color next/prev (OG GuiRegulator), 2/3 = mode toggle. */
    @Override
    public boolean clickMenuButton(PlayerEntity player, int id) {
        switch (id) {
            case 0: {
                int c = this.regulator.color & 0xFF;
                this.regulator.color = (byte) (c >= 16 ? 0 : c + 1);
                break;
            }
            case 1: {
                int c = this.regulator.color & 0xFF;
                this.regulator.color = (byte) (c <= 0 ? 16 : c - 1);
                break;
            }
            case 2:
            case 3:
                this.regulator.mode = (byte) (this.regulator.mode == 0 ? 1 : 0);
                break;
            default:
                return false;
        }
        this.regulator.setChanged();
        return true;
    }

    private static RegulatorBlockEntity getRegulator(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof RegulatorBlockEntity) {
            RegulatorBlockEntity regulator = (RegulatorBlockEntity) be;
            return regulator;
        }
        throw new IllegalStateException("Regulator missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.REGULATOR.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(stack, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 9, 18, false)) {
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
