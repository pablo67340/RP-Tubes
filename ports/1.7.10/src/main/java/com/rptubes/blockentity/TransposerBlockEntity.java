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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

/**
 * Port of TileTranspose — redstone pulse pulls 1 item (or vacuum items) into tubes.
 */
public class TransposerBlockEntity extends CompatTileEntity implements ITubeConnectable {
    protected final TubeBuffer buffer = new TubeBuffer();
    protected boolean powered;
    protected boolean active;
    protected int tickCooldown;

    public TransposerBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, TransposerBlockEntity be) {
        if (be.tickCooldown > 0) {
            --be.tickCooldown;
        }
        if (be.hasBuffered()) {
            // The cooldown only paces retries of a plugged buffer; it must not
            // delay the Active reset below, or fast redstone pulses get ignored.
            if (be.tickCooldown > 0) {
                return;
            }
            // OG onTileTick: retry the buffer, reschedule in 10 if still plugged
            be.drainBuffer();
            be.tickCooldown = be.hasBuffered() ? 10 : 5;
            return;
        }
        if (!be.powered && be.active) {
            be.setActive(false);
        }
    }

    /** Whether any internal buffer still holds items (sorter adds channel buffers). */
    protected boolean hasBuffered() {
        return !this.buffer.isEmpty();
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
            this.markDirty();
            if (this.active) {
                return;
            }
            this.setActive(true);
            ForgeDirection back = this.getFacing().getOpposite();
            BlockPos src = this.getPos().offset(back);
            if (this.canSuckAir(src)) {
                this.doSuck();
            } else {
                this.handleExtract(src, this.getFacing());
            }
        } else {
            if (this.active && this.tickCooldown <= 0) {
                this.tickCooldown = 5;
            }
            this.powered = false;
            this.markDirty();
        }
    }

    protected boolean canSuckAir(BlockPos pos) {
        if (this.getWorld().isSideSolid(pos.getX(), pos.getY(), pos.getZ(), this.getFacing())) {
            return false;
        }
        TileEntity te = this.getWorld().getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        return te == null || (!(te instanceof ITubeConnectable) && MachineLib.getHandler(this.getWorld(), pos, this.getFacing()) == null);
    }

    protected boolean handleExtract(BlockPos src, ForgeDirection extractSide) {
        ItemStack extracted = this.extractFrom(src, extractSide);
        if (com.rptubes.util.Stacks.isEmpty(extracted)) {
            return false;
        }
        this.addToBuffer(extracted);
        this.drainBuffer();
        return true;
    }

    protected ItemStack extractFrom(BlockPos src, ForgeDirection extractSide) {
        return MachineLib.extractOne(this.getWorld(), src, extractSide);
    }

    protected void addToBuffer(ItemStack stack) {
        this.buffer.addNew(stack);
    }

    protected void doSuck() {
        this.suckEntities(this.getSuckBox());
    }

    protected AxisAlignedBB getSuckBox() {
        ForgeDirection face = this.getFacing().getOpposite();
        BlockPos p = this.getPos();
        return AxisAlignedBB.getBoundingBox(p.getX(), p.getY(), p.getZ(), p.getX() + 1, p.getY() + 1, p.getZ() + 1)
                .expand(0.55, 0.55, 0.55)
                .offset(face.offsetX * 0.6, face.offsetY * 0.6, face.offsetZ * 0.6);
    }

    protected boolean suckFilter(ItemStack stack) {
        return true;
    }

    protected void suckEntities(AxisAlignedBB box) {
        boolean trig = false;
        @SuppressWarnings("unchecked")
        java.util.List<EntityItem> found = this.getWorld().getEntitiesWithinAABB(EntityItem.class, box);
        for (EntityItem item : found) {
            ItemStack stack = item.getEntityItem();
            if (item.isDead || com.rptubes.util.Stacks.isEmpty(stack) || !this.suckFilter(stack)) {
                continue;
            }
            this.addToBuffer(stack.copy());
            item.setDead();
            trig = true;
        }
        if (trig) {
            this.setActive(true);
            this.drainBuffer();
            this.tickCooldown = 5;
        }
    }

    public void drainBuffer() {
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

    protected void setActive(boolean value) {
        if (this.active == value || this.getWorld() == null) {
            this.active = value;
            return;
        }
        this.active = value;
        MachineBlock.setActive(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, value);
        this.markDirty();
    }

    @Override
    public int getTubeConnectableSides() {
        ForgeDirection face = this.getFacing();
        return (1 << face.ordinal()) | (1 << face.getOpposite().ordinal());
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
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            this.buffer.addBounce(ti);
            this.setActive(true);
            this.tickCooldown = 5;
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            if (this.powered || !this.buffer.isEmpty()) {
                return false;
            }
            this.addToBuffer(ti.item);
            this.setActive(true);
            this.tickCooldown = 5;
            this.drainBuffer();
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            return this.buffer.isEmpty() && !this.powered;
        }
        return false;
    }

    @Override
    public int tubeWeight(int side, int state) {
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
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
