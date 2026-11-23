package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/** Redstone tube — same item routing as pneumatic, plus a cached redstone power level. */
public class RedstoneTubeBlockEntity extends PneumaticTubeBlockEntity {
    private int power;

    public RedstoneTubeBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.REDSTONE_TUBE.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.REDSTONE_TUBE, pos, state);
        *///?}
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
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
        tag.putInt("Power", this.power);
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.power = tag.getInt("Power");
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.power = tag.getInt("Power");
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.power = tag.getIntOr("Power", 0);
    *///?}
    }
}
