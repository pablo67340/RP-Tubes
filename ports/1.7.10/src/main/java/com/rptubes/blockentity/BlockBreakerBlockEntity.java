package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

/** Port of TileBreaker — pulse breaks the front block and ejects drops into tubes. */
public class BlockBreakerBlockEntity extends CompatTileEntity implements ITubeConnectable {
    private final TubeBuffer buffer = new TubeBuffer();
    private boolean powered;
    private boolean active;
    private int tickCooldown;

    public BlockBreakerBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, BlockBreakerBlockEntity be) {
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
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        boolean signal = this.getWorld().isBlockIndirectlyGettingPowered(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
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
        ForgeDirection breakDir = this.getFacing().getOpposite();
        BlockPos breakPos = this.getPos().offset(breakDir);
        int bx = breakPos.getX(), by = breakPos.getY(), bz = breakPos.getZ();
        net.minecraft.block.Block block = this.getWorld().getBlock(bx, by, bz);
        int meta = this.getWorld().getBlockMetadata(bx, by, bz);
        if (block.getMaterial() == net.minecraft.block.material.Material.air
                || block == Blocks.bedrock
                || block.getBlockHardness(this.getWorld(), bx, by, bz) < 0) {
            return;
        }
        java.util.ArrayList<net.minecraft.item.ItemStack> drops =
                block.getDrops(this.getWorld(), bx, by, bz, meta, 0);
        // break effect + removal, without vanilla dropping the items itself
        this.getWorld().playAuxSFX(2001, bx, by, bz,
                net.minecraft.block.Block.getIdFromBlock(block) + (meta << 12));
        this.getWorld().setBlockToAir(bx, by, bz);
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
            if (!MachineLib.handleItem(this.getWorld(), ti, this.getPos(), this.getFacing())) {
                this.buffer.plugged = true;
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                return;
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
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
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
    public void updateEntity() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        serverTick(this.getWorld(), this.getPos(), this);
    }
}
