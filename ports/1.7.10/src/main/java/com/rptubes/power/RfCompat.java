package com.rptubes.power;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Talks to CoFH RF API blocks and items when an RF mod is installed. Every cofh reference
 * lives in a nested class, which only loads once the matching API class is known to exist.
 */
public final class RfCompat {
    private static final boolean RECEIVER = present("cofh.api.energy.IEnergyReceiver");
    private static final boolean HANDLER = present("cofh.api.energy.IEnergyHandler");
    private static final boolean ITEM = present("cofh.api.energy.IEnergyContainerItem");

    private RfCompat() {
    }

    private static boolean present(String name) {
        try {
            Class.forName(name, false, RfCompat.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /** Offers {@code amount} RF to {@code te} entering from {@code from}; returns RF accepted. */
    public static long push(TileEntity te, ForgeDirection from, long amount) {
        int offer = clamp(amount);
        if (RECEIVER) {
            int accepted = Receiver.push(te, from, offer);
            if (accepted >= 0) {
                return accepted;
            }
        }
        if (HANDLER) {
            int accepted = Handler.push(te, from, offer);
            if (accepted >= 0) {
                return accepted;
            }
        }
        return 0;
    }

    public static long chargeItem(ItemStack stack, long amount) {
        return ITEM ? Items.receive(stack, clamp(amount)) : 0;
    }

    public static long drainItem(ItemStack stack, long amount) {
        return ITEM ? Items.extract(stack, clamp(amount)) : 0;
    }

    static int clamp(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, value));
    }

    private static final class Receiver {
        static int push(TileEntity te, ForgeDirection from, int amount) {
            if (!(te instanceof cofh.api.energy.IEnergyReceiver)) {
                return -1;
            }
            cofh.api.energy.IEnergyReceiver receiver = (cofh.api.energy.IEnergyReceiver) te;
            return receiver.canConnectEnergy(from) ? receiver.receiveEnergy(from, amount, false) : 0;
        }
    }

    private static final class Handler {
        static int push(TileEntity te, ForgeDirection from, int amount) {
            if (!(te instanceof cofh.api.energy.IEnergyHandler)) {
                return -1;
            }
            cofh.api.energy.IEnergyHandler handler = (cofh.api.energy.IEnergyHandler) te;
            return handler.canConnectEnergy(from) ? handler.receiveEnergy(from, amount, false) : 0;
        }
    }

    private static final class Items {
        static long receive(ItemStack stack, int amount) {
            if (stack == null || !(stack.getItem() instanceof cofh.api.energy.IEnergyContainerItem)) {
                return 0;
            }
            return ((cofh.api.energy.IEnergyContainerItem) stack.getItem()).receiveEnergy(stack, amount, false);
        }

        static long extract(ItemStack stack, int amount) {
            if (stack == null || !(stack.getItem() instanceof cofh.api.energy.IEnergyContainerItem)) {
                return 0;
            }
            return ((cofh.api.energy.IEnergyContainerItem) stack.getItem()).extractEnergy(stack, amount, false);
        }
    }
}
