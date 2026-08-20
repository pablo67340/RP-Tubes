package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Port of TileBreaker — pulse breaks the front block and ejects drops into tubes. */
public class BlockBreakerBlockEntity extends BlockEntity implements ITubeConnectable {
    private final TubeBuffer buffer = new TubeBuffer();
    private boolean powered;
    private boolean active;
    private int tickCooldown;

    public BlockBreakerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_BREAKER, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockBreakerBlockEntity be) {
        if (be.tickCooldown > 0) {
            --be.tickCooldown;
        }
        if (!be.buffer.isEmpty()) {
            be.drainBuffer();
            be.tickCooldown = be.buffer.isEmpty() ? 5 : 10;
            return;
        }
        if (!be.powered && be.active) {
            be.setActive(false);
        }
    }

    public void onNeighborChange() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        boolean signal = this.level.hasNeighborSignal(this.worldPosition);
        if (signal) {
            if (this.powered) {
                return;
            }
            this.powered = true;
            if (!this.active) {
                this.setActive(true);
                this.breakFront();
            }
        } else {
            if (this.active && this.tickCooldown <= 0) {
                this.tickCooldown = 5;
            }
            this.powered = false;
        }
        this.setChanged();
    }

    private void breakFront() {
        Direction breakDir = this.getFacing().getOpposite();
        BlockPos breakPos = this.worldPosition.relative(breakDir);
        BlockState state = this.level.getBlockState(breakPos);
        if (state.isAir() || state.is(Blocks.BEDROCK) || state.getDestroySpeed(this.level, breakPos) < 0) {
            return;
        }
        BlockEntity brokenBe = this.level.getBlockEntity(breakPos);
        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) this.level, breakPos, brokenBe, null, ItemStack.EMPTY);
        this.level.destroyBlock(breakPos, false);
        this.buffer.addAll(drops);
        this.drainBuffer();
        if (!this.buffer.isEmpty()) {
            this.tickCooldown = 5;
        }
        this.setChanged();
    }

    private void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!MachineLib.handleItem(this.level, ti, this.worldPosition, this.getFacing())) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                return;
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
    public int getTubeConnectableSides() {
        return 1 << this.getFacing().ordinal();
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        if (side == this.getFacing().ordinal() && state == 2) {
            this.buffer.addBounce(ti);
            this.setActive(true);
            this.tickCooldown = 5;
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        return side == this.getFacing().ordinal() && state == 2;
    }

    @Override
    public int tubeWeight(int side, int state) {
        if (side == this.getFacing().ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        this.buffer.writeToNBT(tag, this.level != null ? this.level.registryAccess() : null);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBooleanOr("Powered", false);
        this.active = tag.getBooleanOr("Active", false);
    }

    @Override
    public void setRemoved() {
        this.buffer.onRemove(this);
        super.setRemoved();
    }
}
