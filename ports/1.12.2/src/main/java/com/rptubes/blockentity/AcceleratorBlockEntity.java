package com.rptubes.blockentity;

import com.rptubes.tube.TubeItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * RP2 Accelerator: spends power to speed up each item passing through it. Plain tubes bleed the
 * speed off again within a few blocks; MagTubes keep it.
 */
public class AcceleratorBlockEntity extends PneumaticTubeBlockEntity {
    /** Item power that moves one block per tick, the fastest a tube can carry anything. */
    public static final short BOOSTED_POWER = 112;

    private final MachineEnergy energy = new MachineEnergy();

    @Override
    public boolean tryPaint(int color) {
        return false;
    }

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
        if (this.world == null || this.world.isRemote || item.power >= BOOSTED_POWER) {
            return;
        }
        if (!this.energy.pay(true)) {
            return;
        }
        item.power = BOOSTED_POWER;
        this.getTubeFlow().pendingSync.add(item);
        this.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.energy.write(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.energy.read(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return this.energy.isEnergyCap(cap) || super.hasCapability(cap, side);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        T energy = this.energy.cast(cap, side);
        if (energy != null) {
            return energy;
        }
        return super.getCapability(cap, side);
    }
}
