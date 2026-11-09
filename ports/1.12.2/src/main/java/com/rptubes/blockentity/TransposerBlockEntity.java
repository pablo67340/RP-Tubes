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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Port of TileTranspose — redstone pulse pulls 1 item (or vacuum items) into tubes.
 */
public class TransposerBlockEntity extends TileEntity implements net.minecraft.util.ITickable, ITubeConnectable {
    protected final TubeBuffer buffer = new TubeBuffer();
    protected boolean powered;
    protected boolean active;
    protected int tickCooldown;

    public TransposerBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, TransposerBlockEntity be) {
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
        if (!be.powered && !be.hasBuffered() && be.tickCooldown <= 0) {
            be.tryEnergyAutoExtract();
        }
    }

    /** Whether any internal buffer still holds items (sorter adds channel buffers). */
    protected boolean hasBuffered() {
        return !this.buffer.isEmpty();
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
            this.markDirty();
            if (this.active) {
                return;
            }
            this.setActive(true);
            if (this.payForExtract(false)) {
                this.startExtract();
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
        IBlockState state = this.world.getBlockState(pos);
        if (state.isSideSolid(this.world, pos, this.getFacing())) {
            return false;
        }
        TileEntity te = this.world.getTileEntity(pos);
        return te == null || (!(te instanceof ITubeConnectable) && MachineLib.getHandler(this.world, pos, this.getFacing()) == null);
    }

    /** Filter / Retriever / Sorter override. Transposer itself does not use RF. */
    protected boolean payForExtract(boolean mustHaveEnergy) {
        return !mustHaveEnergy;
    }

    protected void tryEnergyAutoExtract() {
    }

    protected void startExtract() {
        EnumFacing back = this.getFacing().getOpposite();
        BlockPos src = this.pos.offset(back);
        if (this.canSuckAir(src)) {
            this.doSuck();
        } else {
            this.handleExtract(src, this.getFacing());
        }
    }

    protected boolean handleExtract(BlockPos src, EnumFacing extractSide) {
        ItemStack extracted = this.extractFrom(src, extractSide);
        if (extracted.isEmpty()) {
            return false;
        }
        this.addToBuffer(extracted);
        this.drainBuffer();
        return true;
    }

    protected ItemStack extractFrom(BlockPos src, EnumFacing extractSide) {
        return MachineLib.extractOne(this.world, src, extractSide);
    }

    protected void addToBuffer(ItemStack stack) {
        this.buffer.addNew(stack);
    }

    protected void doSuck() {
        this.suckEntities(this.getSuckBox());
    }

    protected AxisAlignedBB getSuckBox() {
        EnumFacing face = this.getFacing().getOpposite();
        BlockPos p = this.pos;
        return new AxisAlignedBB(p).grow(0.55).offset(face.getXOffset() * 0.6, face.getYOffset() * 0.6, face.getZOffset() * 0.6);
    }

    protected boolean suckFilter(ItemStack stack) {
        return true;
    }

    protected void suckEntities(AxisAlignedBB box) {
        boolean trig = false;
        for (EntityItem item : this.world.getEntitiesWithinAABB(EntityItem.class, box)) {
            ItemStack stack = item.getItem();
            if (item.isDead || stack.isEmpty() || !this.suckFilter(stack)) {
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

    protected void setActive(boolean value) {
        if (this.active == value || this.world == null) {
            this.active = value;
            return;
        }
        this.active = value;
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getValue(MachineBlock.ACTIVE) != value) {
            this.world.setBlockState(this.pos, state.withProperty(MachineBlock.ACTIVE, value), 2);
        }
        this.markDirty();
    }

    @Override
    public int getTubeConnectableSides() {
        EnumFacing face = this.getFacing();
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
        EnumFacing face = this.getFacing();
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
        EnumFacing face = this.getFacing();
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
        EnumFacing face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
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

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                                 net.minecraft.block.state.IBlockState oldState,
                                 net.minecraft.block.state.IBlockState newState) {
        // Default Forge behavior recreates modded tile entities on ANY state
        // change (e.g. lit/active toggles), silently wiping their contents.
        return oldState.getBlock() != newState.getBlock();
    }
}
