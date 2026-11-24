package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.TubeItem;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

/**
 * RP2 Accelerator: spends power to speed up each item passing through it. Plain tubes bleed the
 * speed off again within a few blocks; MagTubes keep it.
 */
public class AcceleratorBlockEntity extends PneumaticTubeBlockEntity {
    /** Item power that moves one block per tick, the fastest a tube can carry anything. */
    public static final short BOOSTED_POWER = 112;

    private final MachineEnergy energy = new MachineEnergy();

    public AcceleratorBlockEntity() {
        super(ModBlockEntities.ACCELERATOR.get());
    }

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
        if (this.level == null || this.level.isClientSide || item.power >= BOOSTED_POWER) {
            return;
        }
        if (!this.energy.pay(true)) {
            return;
        }
        item.power = BOOSTED_POWER;
        this.getTubeFlow().pendingSync.add(item);
        this.setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        this.energy.write(tag);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.energy.read(tag);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && this.energy.isEnergyCap(cap)) {
            return this.energy.cast(cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energy.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.energy.revive();
    }
}
