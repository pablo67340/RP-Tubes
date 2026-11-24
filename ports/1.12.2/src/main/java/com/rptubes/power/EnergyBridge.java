package com.rptubes.power;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/** Moves FE between RP Tubes power blocks and Forge Energy. */
public final class EnergyBridge {
    private EnergyBridge() {
    }

    public static IEnergyStorage storage(PowerBuffer buffer) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return (int) buffer.receive(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return (int) buffer.extract(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return clamp(buffer.getStored());
            }

            @Override
            public int getMaxEnergyStored() {
                return clamp(buffer.getCapacity());
            }

            @Override
            public boolean canExtract() {
                return buffer.canExtract();
            }

            @Override
            public boolean canReceive() {
                return buffer.canReceive();
            }
        };
    }

    public static long push(World world, BlockPos pos, EnumFacing dir, long amount) {
        TileEntity be = world.getTileEntity(pos.offset(dir));
        if (be == null || !be.hasCapability(CapabilityEnergy.ENERGY, dir.getOpposite())) {
            return 0;
        }
        IEnergyStorage target = be.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
        if (target == null || !target.canReceive()) {
            return 0;
        }
        return target.receiveEnergy(clamp(amount), false);
    }

    private static IEnergyStorage itemEnergy(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return null;
        }
        return stack.getCapability(CapabilityEnergy.ENERGY, null);
    }

    public static long chargeSlot(IInventory container, int slot, long amount) {
        IEnergyStorage item = itemEnergy(container.getStackInSlot(slot));
        if (item == null || !item.canReceive()) {
            return 0;
        }
        int moved = item.receiveEnergy(clamp(amount), false);
        if (moved > 0) {
            container.markDirty();
        }
        return moved;
    }

    public static long drainSlot(IInventory container, int slot, long amount) {
        IEnergyStorage item = itemEnergy(container.getStackInSlot(slot));
        if (item == null || !item.canExtract()) {
            return 0;
        }
        int moved = item.extractEnergy(clamp(amount), false);
        if (moved > 0) {
            container.markDirty();
        }
        return moved;
    }

    public static long chargePlayerSlot(EntityPlayer player, int slot, long amount) {
        return chargeSlot(player.inventory, slot, amount);
    }

    private static int clamp(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, value));
    }
}
