package com.rptubes.blockentity;

import com.rptubes.tube.TubeItem;
import cpw.mods.fml.common.Optional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * RP2 Accelerator: spends power to speed up each item passing through it. Plain tubes bleed the
 * speed off again within a few blocks; MagTubes keep it.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHAPI|energy")
public class AcceleratorBlockEntity extends PneumaticTubeBlockEntity implements cofh.api.energy.IEnergyHandler {
    /** Item power that moves one block per tick, the fastest a tube can carry anything. */
    public static final short BOOSTED_POWER = 112;

    private final MachineEnergy energy = new MachineEnergy();

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem item) {
        if (!super.tubeItemEnter(side, state, item)) {
            return false;
        }
        this.boost(item);
        return true;
    }

    @Override
    public void addTubeItem(TubeItem item) {
        super.addTubeItem(item);
        this.boost(item);
    }

    private void boost(TubeItem item) {
        if (this.worldObj == null || this.worldObj.isRemote || item.power >= BOOSTED_POWER) {
            return;
        }
        if (!this.energy.pay()) {
            return;
        }
        item.power = BOOSTED_POWER;
        this.getTubeFlow().pendingSync.add(item);
        this.markDirty();
    }

    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        int accepted = this.energy.receive(maxReceive, simulate);
        if (accepted > 0 && !simulate) {
            this.markDirty();
        }
        return accepted;
    }

    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    public int getEnergyStored(ForgeDirection from) {
        return this.energy.getStored();
    }

    public int getMaxEnergyStored(ForgeDirection from) {
        return MachineEnergy.CAPACITY;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.energy.write(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.energy.read(tag);
    }
}
