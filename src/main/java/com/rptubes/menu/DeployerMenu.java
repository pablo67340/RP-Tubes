package com.rptubes.menu;

import com.rptubes.blockentity.DeployerBlockEntity;
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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DeployerMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    //? if fabric && >=1.20.6 {
    /*public DeployerMenu(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, getDeployer(playerInv, pos));
    *///?} else {
    public DeployerMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getDeployer(playerInv, buf));
    //?}
    }

    public DeployerMenu(int id, Inventory playerInv, DeployerBlockEntity deployer) {
        //? if forgelike {
        super(ModMenus.DEPLOYER.get(), id);
        //?} else {
        /*super(ModMenus.DEPLOYER, id);
        *///?}
        this.access = ContainerLevelAccess.create(deployer.getLevel(), deployer.getBlockPos());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(deployer, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        addPlayerSlots(playerInv, 84);
    }

    private void addPlayerSlots(Inventory playerInv, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, y + 58));
        }
    }

    //? if <1.20.1 {
    /*private static DeployerBlockEntity getDeployer(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.getLevel().getBlockEntity(pos);
    *///?} else if fabric && >=1.20.6 {
    /*private static DeployerBlockEntity getDeployer(Inventory inv, BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    *///?} else {
    private static DeployerBlockEntity getDeployer(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
    //?}
        if (be instanceof DeployerBlockEntity deployer) {
            return deployer;
        }
        throw new IllegalStateException("Deployer missing at " + pos);
    }

    @Override
    public boolean stillValid(Player player) {
        //? if forgelike {
        return stillValid(this.access, player, ModBlocks.DEPLOYER.get());
        //?} else {
        /*return stillValid(this.access, player, ModBlocks.DEPLOYER);
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
