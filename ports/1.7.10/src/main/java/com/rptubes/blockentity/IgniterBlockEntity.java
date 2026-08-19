package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

/**
 * Port of TileIgniter — powered: place fire in front; unpowered: extinguish fire/portal.
 */
public class IgniterBlockEntity extends CompatTileEntity {
    private boolean powered;
    private boolean active;
    private boolean delay;
    private int tickCooldown;

    public IgniterBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IgniterBlockEntity be) {
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
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        boolean signal = this.getWorld().isBlockIndirectlyGettingPowered(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
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
        if (this.getWorld() == null) {
            return;
        }
        // Working face = FACING (same convention as Deployer / OG Rotation^1)
        BlockPos target = this.getPos().offset(this.getFacing());
        net.minecraft.block.Block targetBlock = this.getWorld().getBlock(target.getX(), target.getY(), target.getZ());
        if (this.active) {
            if (targetBlock.getMaterial() == net.minecraft.block.material.Material.air) {
                this.getWorld().setBlock(target.getX(), target.getY(), target.getZ(), Blocks.fire, 0, 3);
            }
        } else {
            if (targetBlock == Blocks.fire || targetBlock == Blocks.portal) {
                this.getWorld().setBlockToAir(target.getX(), target.getY(), target.getZ());
            }
        }
    }

    public ForgeDirection getFacing() {
        return MachineBlock.getFacing(this.getWorld().getBlockMetadata(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.getWorld() != null) {
            MachineBlock.setActive(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, value);
        }
        this.markDirty();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
        tag.setBoolean("Delay", this.delay);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
        this.delay = tag.getBoolean("Delay");
    }

    @Override
    public void updateEntity() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        serverTick(this.getWorld(), this.getPos(), this);
    }
}
