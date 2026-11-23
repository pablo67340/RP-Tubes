package com.rptubes.power;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
//? if neoforge && <1.21.11 {
import net.neoforged.neoforge.energy.IEnergyStorage;
//?} else if neoforge {
/*import net.neoforged.neoforge.transfer.energy.EnergyHandler;
*///?} else if forge {
/*import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
*///?}

/** Block entity with an FE buffer that can push into its neighbours. */
public abstract class PowerBlockEntity extends BlockEntity {
    protected final PowerBuffer buffer;
    //? if forge {
    /*private LazyOptional<IEnergyStorage> energyCap;
    *///?}

    protected PowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                               long capacity, long maxReceive, long maxExtract) {
        super(type, pos, state);
        this.buffer = new PowerBuffer(capacity, maxReceive, maxExtract, this::setChanged);
        //? if forge {
        /*this.energyCap = LazyOptional.of(() -> this.buffer.adapter(EnergyBridge::storage));
        *///?}
    }

    public PowerBuffer getBuffer() {
        return this.buffer;
    }

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

    //? if neoforge && <1.21.11 {
    public IEnergyStorage getEnergyStorage(Direction side) {
        return this.buffer.adapter(EnergyBridge::storage);
    }
    //?} else if neoforge {
    /*public EnergyHandler getEnergy(Direction side) {
        return this.buffer.adapter(EnergyBridge::handler);
    }
    *///?} else if forge {
    /*@Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ENERGY) {
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
    *///?}

    @Override
    //? if <1.20.6 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    *///?} else if <1.21.11 {
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    //?} else {
    /*protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
    *///?}
        tag.putLong("Energy", this.buffer.getStored());
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.buffer.setStored(tag.getLong("Energy"));
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.buffer.setStored(tag.getLong("Energy"));
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.buffer.setStored(tag.getLongOr("Energy", 0L));
    *///?}
    }
}
