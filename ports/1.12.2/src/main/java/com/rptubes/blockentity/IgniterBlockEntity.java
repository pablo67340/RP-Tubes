package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

/**
 * Port of TileIgniter — powered: place fire in front; unpowered: extinguish fire/portal.
 */
public class IgniterBlockEntity extends TileEntity implements net.minecraft.util.ITickable {
    private boolean powered;
    private boolean active;
    private boolean delay;
    private int tickCooldown;

    public IgniterBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, IgniterBlockEntity be) {
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
        if (this.world == null || this.world.isRemote) {
            return;
        }
        boolean signal = this.world.isBlockPowered(this.pos);
        if (!signal) {
            if (!this.powered) {
                return;
            }
            this.powered = false;
            if (this.delay) {
                this.markDirty();
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
                this.markDirty();
                return;
            }
            this.setActive(true);
            this.delay = true;
            this.fireAction();
        }
        this.tickCooldown = 5;
        this.markDirty();
    }

    private void fireAction() {
        if (this.world == null) {
            return;
        }
        // Working face = FACING (same convention as Deployer / OG Rotation^1)
        BlockPos target = this.pos.offset(this.getFacing());
        IBlockState targetState = this.world.getBlockState(target);
        if (this.active) {
            if (targetState.getMaterial() == net.minecraft.block.material.Material.AIR) {
                this.world.setBlockState(target, Blocks.FIRE.getDefaultState(), 3);
            }
        } else {
            if (targetState.getBlock() == Blocks.FIRE || targetState.getBlock() == Blocks.PORTAL) {
                this.world.setBlockToAir(target);
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
        tag.setBoolean("Delay", this.delay);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.delay = tag.getBoolean("Delay");
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        serverTick(this.world, this.pos, this.world.getBlockState(this.pos), this);
    }
}
