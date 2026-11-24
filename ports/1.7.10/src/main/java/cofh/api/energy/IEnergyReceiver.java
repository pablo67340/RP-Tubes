package cofh.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

/** Compile-only stub of the CoFH RF API; excluded from the jar. */
public interface IEnergyReceiver extends IEnergyConnection {
    int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate);

    int getEnergyStored(ForgeDirection from);

    int getMaxEnergyStored(ForgeDirection from);
}
