package com.rptubes.power;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/** Block entity with an FE buffer that can push into its neighbours. */
public abstract class PowerBlockEntity extends TileEntity implements ITickableTileEntity {
    protected final PowerBuffer buffer;
    private LazyOptional<IEnergyStorage> energyCap;

    protected PowerBlockEntity(TileEntityType<?> type, long capacity, long maxReceive, long maxExtract) {
        super(type);
        this.buffer = new PowerBuffer(capacity, maxReceive, maxExtract, this::setChanged);
        this.energyCap = LazyOptional.of(() -> this.buffer.adapter(EnergyBridge::storage));
    }

    public PowerBuffer getBuffer() {
        return this.buffer;
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            this.serverTick();
        }
    }

    protected abstract void serverTick();

    /** Whether this block may push energy into the neighbour on {@code dir}. */
    protected boolean canPushTo(Direction dir) {
        return true;
    }

    /** Offers up to {@code budget} FE from the buffer to each neighbour in turn; returns FE sent. */
    protected long pushToNeighbours(long budget) {
        if (this.level == null) {
            return 0;
        }
        long sent = 0;
        for (Direction dir : Direction.values()) {
            long offer = Math.min(budget - sent, this.buffer.getStored());
            if (offer <= 0) {
                break;
            }
            if (!this.canPushTo(dir)) {
                continue;
            }
            long accepted = EnergyBridge.push(this.level, this.worldPosition, dir, offer);
            if (accepted > 0) {
                this.buffer.drain(accepted);
                sent += accepted;
            }
        }
        return sent;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityEnergy.ENERGY) {
            return this.energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyCap.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.energyCap = LazyOptional.of(() -> this.buffer.adapter(EnergyBridge::storage));
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putLong("Energy", this.buffer.getStored());
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.buffer.setStored(tag.getLong("Energy"));
    }
}
