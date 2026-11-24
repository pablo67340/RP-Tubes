package com.rptubes.power;

import com.rptubes.blockentity.AcceleratorBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Moves RF from RP Tubes power blocks into RP Tubes machines and, when installed, RF API blocks. */
public final class EnergyBridge {
    private EnergyBridge() {
    }

    public static long push(World world, int x, int y, int z, ForgeDirection dir, long amount) {
        TileEntity be = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
        if (be == null || amount <= 0) {
            return 0;
        }
        if (be instanceof PowerBlockEntity) {
            return ((PowerBlockEntity) be).getBuffer().receive(amount, false);
        }
        if (be instanceof AcceleratorBlockEntity) {
            return ((AcceleratorBlockEntity) be).receiveEnergy(dir.getOpposite(), RfCompat.clamp(amount), false);
        }
        return RfCompat.push(be, dir.getOpposite(), amount);
    }
}
