package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Port of TileIgniter — powered: place fire in front; unpowered: extinguish fire/portal.
 */
public class IgniterBlockEntity extends BlockEntity {
    private boolean powered;
    private boolean active;
    private boolean delay;
    private int tickCooldown;

    public IgniterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IGNITER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IgniterBlockEntity be) {
        if (be.tickCooldown > 0) {
            --be.tickCooldown;
            if (be.tickCooldown == 0) {
                be.delay = false;
                if (be.active != be.powered) {
                    be.setActive(be.powered);
                    be.fireAction();
                }
            }
        }
    }

    public void onNeighborChange() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        boolean signal = this.level.hasNeighborSignal(this.worldPosition);
        if (!signal) {
            if (!this.powered) {
                return;
            }
            this.powered = false;
            if (this.delay) {
                this.setChanged();
                return;
            }
            this.setActive(false);
            this.delay = true;
            this.fireAction();
        } else {
            if (this.powered) {
                return;
            }
            this.powered = true;
            if (this.delay || this.active) {
                this.setChanged();
                return;
            }
            this.setActive(true);
            this.delay = true;
            this.fireAction();
        }
        this.tickCooldown = 5;
        this.setChanged();
    }

    private void fireAction() {
        if (this.level == null) {
            return;
        }
        // Working face = FACING (same convention as Deployer / OG Rotation^1)
        BlockPos target = this.worldPosition.relative(this.getFacing());
        BlockState targetState = this.level.getBlockState(target);
        if (this.active) {
            if (targetState.isAir()) {
                this.level.setBlock(target, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            }
        } else {
            if (targetState.is(Blocks.FIRE) || targetState.is(Blocks.NETHER_PORTAL)) {
                this.level.removeBlock(target, false);
            }
        }
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(MachineBlock.FACING);
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.level != null) {
            BlockState state = this.getBlockState();
            if (state.getValue(MachineBlock.ACTIVE) != value) {
                this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), Block.UPDATE_CLIENTS);
            }
        }
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
        tag.putBoolean("Delay", this.delay);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.delay = tag.getBoolean("Delay");
    }
}
