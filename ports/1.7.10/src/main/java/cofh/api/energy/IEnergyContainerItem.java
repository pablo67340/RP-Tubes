package cofh.api.energy;

import net.minecraft.item.ItemStack;

/** Compile-only stub of the CoFH RF API; excluded from the jar. */
public interface IEnergyContainerItem {
    int receiveEnergy(ItemStack container, int maxReceive, boolean simulate);

    int extractEnergy(ItemStack container, int maxExtract, boolean simulate);

    int getEnergyStored(ItemStack container);

    int getMaxEnergyStored(ItemStack container);
}
