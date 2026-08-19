package com.rptubes.menu;

import com.rptubes.blockentity.DeployerBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModMenus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class DeployerMenu extends Container {
    private final IWorldPosCallable access;

    public DeployerMenu(int id, PlayerInventory playerInv, PacketBuffer buf) {
        this(id, playerInv, getDeployer(playerInv, buf));
    }

    public DeployerMenu(int id, PlayerInventory playerInv, DeployerBlockEntity deployer) {
        super(ModMenus.DEPLOYER.get(), id);
        this.access = IWorldPosCallable.create(deployer.getLevel(), deployer.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(deployer, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        addPlayerSlots(playerInv, 84);
    }

    private void addPlayerSlots(PlayerInventory playerInv, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, y + 58));
        }
    }

    private static DeployerBlockEntity getDeployer(PlayerInventory inv, PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TileEntity be = inv.player.level.getBlockEntity(pos);
        if (be instanceof DeployerBlockEntity) {
            DeployerBlockEntity deployer = (DeployerBlockEntity) be;
            return deployer;
        }
        throw new IllegalStateException("Deployer missing at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.DEPLOYER.get());
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
