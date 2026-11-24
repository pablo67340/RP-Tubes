package cofh.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

/** Compile-only stub of the CoFH RF API; excluded from the jar. */
public interface IEnergyProvider extends IEnergyConnection {
    int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate);

    int getEnergyStored(ForgeDirection from);

    int getMaxEnergyStored(ForgeDirection from);
}
