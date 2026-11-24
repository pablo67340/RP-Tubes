package com.rptubes.power;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/** Block entity with an FE buffer that can push into its neighbours. */
public abstract class PowerBlockEntity extends TileEntity implements ITickable {
    protected final PowerBuffer buffer;

    protected PowerBlockEntity(long capacity, long maxReceive, long maxExtract) {
        this.buffer = new PowerBuffer(capacity, maxReceive, maxExtract, this::markDirty);
    }

    public PowerBuffer getBuffer() {
        return this.buffer;
    }

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote) {
            this.serverTick();
        }
    }

    protected abstract void serverTick();

    /** Whether this block may push energy into the neighbour on {@code dir}. */
    protected boolean canPushTo(EnumFacing dir) {
        return true;
    }

    /** Offers up to {@code budget} FE from the buffer to each neighbour in turn; returns FE sent. */
    protected long pushToNeighbours(long budget) {
        if (this.world == null) {
            return 0;
        }
        long sent = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            long offer = Math.min(budget - sent, this.buffer.getStored());
            if (offer <= 0) {
                break;
            }
            if (!this.canPushTo(dir)) {
                continue;
            }
            long accepted = EnergyBridge.push(this.world, this.pos, dir, offer);
            if (accepted > 0) {
                this.buffer.drain(accepted);
                sent += accepted;
            }
        }
        return sent;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return cap == CapabilityEnergy.ENERGY || super.hasCapability(cap, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return (T) this.buffer.<IEnergyStorage>adapter(EnergyBridge::storage);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setLong("Energy", this.buffer.getStored());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.buffer.setStored(tag.getLong("Energy"));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
