package com.rptubes.compat;

import com.rptubes.power.PowerBlockEntity;
import com.rptubes.power.PowerBuffer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

/** Team Reborn Energy 0.1.x side of the power blocks. Only loaded when that library is present. */
public final class TrPowerCompat {
    private TrPowerCompat() {
    }

    public static void register() {
        Energy.registerHolder(PowerBlockEntity.class, be -> wrap(((PowerBlockEntity) be).getBuffer()));
    }

    private static EnergyStorage wrap(PowerBuffer buffer) {
        return new EnergyStorage() {
            @Override
            public double getStored(EnergySide face) {
                return buffer.getStored();
            }

            @Override
            public void setStored(double amount) {
                buffer.setStored((long) amount);
                buffer.markChanged();
            }

            @Override
            public double getMaxStoredPower() {
                return buffer.getCapacity();
            }

            @Override
            public EnergyTier getTier() {
                return EnergyTier.HIGH;
            }

            @Override
            public double getMaxInput(EnergySide side) {
                return buffer.canReceive() ? EnergyTier.HIGH.getMaxInput() : 0;
            }

            @Override
            public double getMaxOutput(EnergySide side) {
                return buffer.canExtract() ? EnergyTier.HIGH.getMaxOutput() : 0;
            }
        };
    }

    public static long push(BlockEntity be, Direction side, long amount) {
        if (!Energy.valid(be)) {
            return 0;
        }
        return (long) Energy.of(be).side(EnergySide.fromMinecraft(side)).insert(amount);
    }

    public static boolean isHolder(ItemStack stack) {
        return !stack.isEmpty() && Energy.valid(stack);
    }

    public static long chargeItem(ItemStack stack, long amount) {
        return isHolder(stack) ? (long) Energy.of(stack).insert(amount) : 0;
    }

    public static long drainItem(ItemStack stack, long amount) {
        return isHolder(stack) ? (long) Energy.of(stack).extract(amount) : 0;
    }
}
