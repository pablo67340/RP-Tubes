package com.rptubes.power;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Block entity with an FE buffer that can push into its neighbours. */
public abstract class PowerBlockEntity extends BlockEntity implements TickableBlockEntity {
    protected final PowerBuffer buffer;

    protected PowerBlockEntity(BlockEntityType<?> type, long capacity, long maxReceive, long maxExtract) {
        super(type);
        this.buffer = new PowerBuffer(capacity, maxReceive, maxExtract, this::setChanged);
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
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putLong("Energy", this.buffer.getStored());
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.buffer.setStored(tag.getLong("Energy"));
    }
}
