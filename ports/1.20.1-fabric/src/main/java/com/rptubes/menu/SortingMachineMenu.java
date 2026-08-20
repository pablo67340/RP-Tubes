package com.rptubes.menu;

import com.rptubes.blockentity.SortingMachineBlockEntity;
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

public class SortingMachineMenu extends AbstractContainerMenu {
    private final SortingMachineBlockEntity sorter;
    private final ContainerLevelAccess access;

    public SortingMachineMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getSorter(playerInv, buf));
    }

    public SortingMachineMenu(int id, Inventory playerInv, SortingMachineBlockEntity sorter) {
        super(ModMenus.SORTING_MACHINE, id);
        this.sorter = sorter;
        this.access = ContainerLevelAccess.create(sorter.getLevel(), sorter.getBlockPos());

        // OG ContainerSorter: 8 cols x 5 rows at (26 + 18*col, 18 + 18*row)
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                this.addSlot(new Slot(sorter, col + row * 8, 26 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 198));
        }

        for (int i = 0; i < 8; i++) {
            final int n = i;
            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return sorter.colors[n];
                }

                @Override
                public void set(int value) {
                    sorter.colors[n] = (byte) value;
                }
            });
        }
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return sorter.column;
            }

            @Override
            public void set(int value) {
                sorter.column = (byte) value;
            }
        });
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return sorter.mode;
            }

            @Override
            public void set(int value) {
                sorter.mode = (byte) value;
            }
        });
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return sorter.defcolor;
            }

            @Override
            public void set(int value) {
                sorter.defcolor = (byte) value;
            }
        });
    }

    private static SortingMachineBlockEntity getSorter(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof SortingMachineBlockEntity sorter) {
            return sorter;
        }
        throw new IllegalStateException("Sorting machine missing at " + pos);
    }

    public int getColor(int column) {
        return this.sorter.colors[column] & 0xFF;
    }

    public int getColumn() {
        return this.sorter.column;
    }

    public int getMode() {
        return this.sorter.mode;
    }

    public int getDefColor() {
        return this.sorter.defcolor & 0xFF;
    }

    /**
     * 0/1 = mode next/prev, 10+n / 20+n = column n color next/prev,
     * 30/31 = default color next/prev (OG GuiSorter interactions).
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 || id == 1) {
            int mode = this.sorter.mode + (id == 0 ? 1 : -1);
            if (mode > 4) {
                mode = 0;
            }
            if (mode < 0) {
                mode = 4;
            }
            this.sorter.mode = (byte) mode;
        } else if (id >= 10 && id < 18) {
            int n = id - 10;
            this.sorter.colors[n] = (byte) (this.sorter.colors[n] + 1 > 16 ? 0 : this.sorter.colors[n] + 1);
        } else if (id >= 20 && id < 28) {
            int n = id - 20;
            this.sorter.colors[n] = (byte) (this.sorter.colors[n] - 1 < 0 ? 16 : this.sorter.colors[n] - 1);
        } else if (id == 30) {
            this.sorter.defcolor = (byte) (this.sorter.defcolor + 1 > 16 ? 0 : this.sorter.defcolor + 1);
        } else if (id == 31) {
            this.sorter.defcolor = (byte) (this.sorter.defcolor - 1 < 0 ? 16 : this.sorter.defcolor - 1);
        } else {
            return false;
        }
        this.sorter.setChanged();
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.SORTING_MACHINE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 40) {
                if (!this.moveItemStackTo(stack, 40, 76, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 40, false)) {
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
