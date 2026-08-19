package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

import java.util.List;

/** Port of TileBreaker — pulse breaks the front block and ejects drops into tubes. */
public class BlockBreakerBlockEntity extends TileEntity implements net.minecraft.util.ITickable, ITubeConnectable {
    private final TubeBuffer buffer = new TubeBuffer();
    private boolean powered;
    private boolean active;
    private int tickCooldown;

    public BlockBreakerBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, BlockBreakerBlockEntity be) {
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
        if (this.world == null || this.world.isRemote) {
            return;
        }
        boolean signal = this.world.isBlockPowered(this.pos);
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
        this.markDirty();
    }

    private void breakFront() {
        EnumFacing breakDir = this.getFacing().getOpposite();
        BlockPos breakPos = this.pos.offset(breakDir);
        IBlockState state = this.world.getBlockState(breakPos);
        if (state.getMaterial() == net.minecraft.block.material.Material.AIR || state.getBlock() == Blocks.BEDROCK || state.getBlockHardness(this.world, breakPos) < 0) {
            return;
        }
        net.minecraft.util.NonNullList<ItemStack> drops = net.minecraft.util.NonNullList.create();
        state.getBlock().getDrops(drops, this.world, breakPos, state, 0);
        this.world.destroyBlock(breakPos, false);
        this.buffer.addAll(drops);
        this.drainBuffer();
        if (!this.buffer.isEmpty()) {
            this.tickCooldown = 5;
        }
        this.markDirty();
    }

    private void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!MachineLib.handleItem(this.world, ti, this.pos, this.getFacing())) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                return;
            }
        }
    }

    public EnumFacing getFacing() {
        return this.world.getBlockState(this.pos).getValue(MachineBlock.FACING);
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getValue(MachineBlock.ACTIVE) != value) {
                this.world.setBlockState(this.pos, state.withProperty(MachineBlock.ACTIVE, value), 2);
            }
        }
        this.markDirty();
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    public void dropContents() {
        this.buffer.onRemove(this);
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        serverTick(this.world, this.pos, this.world.getBlockState(this.pos), this);
    }
}
