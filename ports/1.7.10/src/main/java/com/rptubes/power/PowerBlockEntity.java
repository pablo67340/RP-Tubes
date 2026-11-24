package com.rptubes.power;

import com.rptubes.blockentity.CompatTileEntity;
import cpw.mods.fml.common.Optional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/** Block entity with an RF buffer that can push into its neighbours. */
@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHAPI|energy")
public abstract class PowerBlockEntity extends CompatTileEntity implements cofh.api.energy.IEnergyHandler {
    protected final PowerBuffer buffer;

    protected PowerBlockEntity(long capacity, long maxReceive, long maxExtract) {
        this.buffer = new PowerBuffer(capacity, maxReceive, maxExtract, this::markDirty);
    }

    public PowerBuffer getBuffer() {
        return this.buffer;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.serverTick();
        }
    }

    protected abstract void serverTick();

    /** Whether this block may push energy into the neighbour on {@code dir}. */
    protected boolean canPushTo(ForgeDirection dir) {
        return true;
    }

    /** Offers up to {@code budget} RF from the buffer to each neighbour in turn; returns RF sent. */
    protected long pushToNeighbours(long budget) {
        if (this.worldObj == null) {
            return 0;
        }
        long sent = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            long offer = Math.min(budget - sent, this.buffer.getStored());
            if (offer <= 0) {
                break;
            }
            if (!this.canPushTo(dir)) {
                continue;
            }
            long accepted = EnergyBridge.push(this.worldObj, this.xCoord, this.yCoord, this.zCoord, dir, offer);
            if (accepted > 0) {
                this.buffer.drain(accepted);
                sent += accepted;
            }
        }
        return sent;
    }

    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return (int) this.buffer.receive(maxReceive, simulate);
    }

    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return (int) this.buffer.extract(maxExtract, simulate);
    }

    public int getEnergyStored(ForgeDirection from) {
        return RfCompat.clamp(this.buffer.getStored());
    }

    public int getMaxEnergyStored(ForgeDirection from) {
        return RfCompat.clamp(this.buffer.getCapacity());
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setLong("Energy", this.buffer.getStored());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.buffer.setStored(tag.getLong("Energy"));
    }
}
