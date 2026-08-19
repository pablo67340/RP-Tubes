package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Port of TileTranspose — redstone pulse pulls 1 item (or vacuum items) into tubes.
 */
public class TransposerBlockEntity extends TileEntity implements net.minecraft.tileentity.ITickableTileEntity, ITubeConnectable {
    protected final TubeBuffer buffer = new TubeBuffer();
    protected boolean powered;
    protected boolean active;
    protected int tickCooldown;

    public TransposerBlockEntity() {
        super(ModBlockEntities.TRANSPOSER.get());
    }

    protected TransposerBlockEntity(net.minecraft.tileentity.TileEntityType<?> type) {
        super(type);
    }

    public static void serverTick(World level, BlockPos pos, BlockState state, TransposerBlockEntity be) {
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
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        boolean signal = this.level.hasNeighborSignal(this.worldPosition);
        if (signal) {
            if (this.powered) {
                return;
            }
            this.powered = true;
            this.setChanged();
            if (this.active) {
                return;
            }
            this.setActive(true);
            Direction back = this.getFacing().getOpposite();
            BlockPos src = this.worldPosition.relative(back);
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
            this.setChanged();
        }
    }

    protected boolean canSuckAir(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        if (state.isFaceSturdy(this.level, pos, this.getFacing())) {
            return false;
        }
        TileEntity te = this.level.getBlockEntity(pos);
        return te == null || (!(te instanceof ITubeConnectable) && MachineLib.getHandler(this.level, pos, this.getFacing()) == null);
    }

    protected boolean handleExtract(BlockPos src, Direction extractSide) {
        ItemStack extracted = this.extractFrom(src, extractSide);
        if (extracted.isEmpty()) {
            return false;
        }
        this.addToBuffer(extracted);
        this.drainBuffer();
        return true;
    }

    protected ItemStack extractFrom(BlockPos src, Direction extractSide) {
        return MachineLib.extractOne(this.level, src, extractSide);
    }

    protected void addToBuffer(ItemStack stack) {
        this.buffer.addNew(stack);
    }

    protected void doSuck() {
        this.suckEntities(this.getSuckBox());
    }

    protected AxisAlignedBB getSuckBox() {
        Direction face = this.getFacing().getOpposite();
        BlockPos p = this.worldPosition;
        return new AxisAlignedBB(p).inflate(0.55).move(face.getStepX() * 0.6, face.getStepY() * 0.6, face.getStepZ() * 0.6);
    }

    protected boolean suckFilter(ItemStack stack) {
        return true;
    }

    protected void suckEntities(AxisAlignedBB box) {
        boolean trig = false;
        for (Entity ent : this.level.getEntities((Entity) null, box, e -> e instanceof ItemEntity)) {
            ItemEntity item = (ItemEntity) ent;
            ItemStack stack = item.getItem();
            if (stack.isEmpty() || !this.suckFilter(stack)) {
                continue;
            }
            this.addToBuffer(stack.copy());
            item.remove();
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

    protected void setActive(boolean value) {
        if (this.active == value || this.level == null) {
            this.active = value;
            return;
        }
        this.active = value;
        BlockState state = this.getBlockState();
        if (state.getValue(MachineBlock.ACTIVE) != value) {
            this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), 2);
        }
        this.setChanged();
    }

    @Override
    public int getTubeConnectableSides() {
        Direction face = this.getFacing();
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
        Direction face = this.getFacing();
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
        Direction face = this.getFacing();
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
        Direction face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        this.buffer.writeToNBT(tag);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.buffer.readFromNBT(tag);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    @Override
    public void setRemoved() {
        this.buffer.onRemove(this);
        super.setRemoved();
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        serverTick(this.level, this.worldPosition, this.getBlockState(), this);
    }
}
