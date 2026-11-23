package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.TubeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
//? if <1.21.11 {
import net.minecraft.nbt.CompoundTag;
//?}
//? if forge {
/*import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
*///?} else if neoforge && <1.21.11 {
import net.neoforged.neoforge.energy.IEnergyStorage;
//?}

/**
 * RP2 Accelerator: spends power to speed up each item passing through it. Plain tubes bleed the
 * speed off again within a few blocks; MagTubes keep it.
 */
public class AcceleratorBlockEntity extends PneumaticTubeBlockEntity {
    /** Item power that moves one block per tick, the fastest a tube can carry anything. */
    public static final short BOOSTED_POWER = 112;

    private final MachineEnergy energy = new MachineEnergy();

    public AcceleratorBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.ACCELERATOR.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.ACCELERATOR, pos, state);
        *///?}
    }

    //? if neoforge && <1.21.11 {
    public IEnergyStorage getEnergyStorage() {
        return this.energy;
    }
    //?} else {
    /*public MachineEnergy getEnergy() {
        return this.energy;
    }
    *///?}

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
        //? if <1.21.11 {
        if (this.level == null || this.level.isClientSide || item.power >= BOOSTED_POWER) {
        //?} else {
        /*if (this.level == null || this.level.isClientSide() || item.power >= BOOSTED_POWER) {
        *///?}
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
        this.energy.write(tag);
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
    *///?}
        this.energy.read(tag);
    }
    //? if forge {

    /*@Override
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
    *///?}
}
