package com.rptubes.power;

import com.rptubes.blockentity.AcceleratorBlockEntity;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.MachineEnergy;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Moves energy from RP Tubes power blocks into RP Tubes machines directly, and into anything
 * else through Team Reborn Energy 0.1.x when it is installed.
 */
public final class EnergyBridge {
    private static final boolean TR_ENERGY = classExists("team.reborn.energy.Energy");

    private EnergyBridge() {
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name, false, EnergyBridge.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static MachineEnergy machineEnergy(Object be) {
        if (be instanceof FilterBlockEntity) {
            return ((FilterBlockEntity) be).getEnergy();
        }
        if (be instanceof SortingMachineBlockEntity) {
            return ((SortingMachineBlockEntity) be).getEnergy();
        }
        if (be instanceof AcceleratorBlockEntity) {
            return ((AcceleratorBlockEntity) be).getEnergy();
        }
        return null;
    }

    public static long push(Level level, BlockPos pos, Direction dir, long amount) {
        BlockEntity be = level.getBlockEntity(pos.relative(dir));
        if (be == null) {
            return 0;
        }
        if (be instanceof PowerBlockEntity) {
            return ((PowerBlockEntity) be).getBuffer().receive(amount, false);
        }
        MachineEnergy machine = machineEnergy(be);
        if (machine != null) {
            long accepted = machine.receive(amount, false);
            if (accepted > 0) {
                be.setChanged();
            }
            return accepted;
        }
        return TR_ENERGY ? com.rptubes.compat.TrPowerCompat.push(be, dir.getOpposite(), amount) : 0;
    }

    public static long chargeSlot(Container container, int slot, long amount) {
        if (!TR_ENERGY) {
            return 0;
        }
        long moved = com.rptubes.compat.TrPowerCompat.chargeItem(container.getItem(slot), amount);
        if (moved > 0) {
            container.setChanged();
        }
        return moved;
    }

    public static long drainSlot(Container container, int slot, long amount) {
        if (!TR_ENERGY) {
            return 0;
        }
        long moved = com.rptubes.compat.TrPowerCompat.drainItem(container.getItem(slot), amount);
        if (moved > 0) {
            container.setChanged();
        }
        return moved;
    }

    public static long chargePlayerSlot(Player player, int slot, long amount) {
        return chargeSlot(player.inventory, slot, amount);
    }
}
