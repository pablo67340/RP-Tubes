package com.rptubes.tube;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Port of eloraam.core.TubeFlow — advances items through a tube block each tick.
 */
public abstract class TubeFlow {
    public LinkedList<TubeItem> contents = new LinkedList<>();
    /**
     * Server-side item events the client can't predict (network entries,
     * junction routing, bounces). Drained once per tick into one small
     * upsert packet; the client merges these into its own simulation.
     */
    public final List<TubeItem> pendingSync = new ArrayList<>();

    public abstract boolean schedule(TubeItem item, TubeScheduleContext ctx);

    public boolean handleItem(TubeItem item, TubeScheduleContext ctx) {
        return false;
    }

    public abstract TileEntity getParent();

    public boolean update() {
        boolean changed = false;
        if (this.contents.isEmpty()) {
            return false;
        }

        TubeScheduleContext ctx = new TubeScheduleContext(this.getParent());
        Iterator<TubeItem> it = this.contents.iterator();
        ctx.tii = it;

        while (it.hasNext()) {
            TubeItem ti = it.next();
            ti.lastProgress = ti.progress;
            ti.progress = (short) (ti.progress + (ti.power + 16));
            if (ti.progress < 128) {
                continue;
            }
            if (ti.power > 0) {
                ti.power = (short) (ti.power - 1);
            }
            changed = true;
            if (!ti.scheduled) {
                if (!this.schedule(ti, ctx)) {
                    it.remove();
                } else {
                    // New leg starts at 0; back-date for seamless interpolation
                    ti.lastProgress = (short) (ti.progress - 16);
                }
                continue;
            }
            it.remove();
            ctx.tir.add(ti);
        }

        if (ctx.world.isRemote) {
            // EnumHand items over to the next tube locally so movement is seamless;
            // the server sync packet corrects any divergence. Items entering
            // non-tube destinations simply vanish until the server confirms.
            for (TubeItem ti : ctx.tir) {
                if (ti.side < 0 || (ctx.cons & (1 << ti.side)) == 0) {
                    continue;
                }
                TileEntity be = ctx.world.getTileEntity(ctx.wc.offset(EnumFacing.byIndex(ti.side)));
                if (be instanceof ITubeFlow) {
                    ITubeFlow flow = (ITubeFlow) be;
                    flow.addTubeItem(ti);
                }
            }
            return changed;
        }

        for (TubeItem ti : ctx.tir) {
            if (ti.side < 0 || (ctx.cons & (1 << ti.side)) == 0) {
                if (ctx.cons == 0) {
                    MachineLib.ejectItem(ctx.world, ctx.wc, ti.item, EnumFacing.UP);
                    continue;
                }
                ti.side = (byte) Integer.numberOfTrailingZeros(ctx.cons);
                ti.progress = 128;
                ti.scheduled = false;
                this.contents.add(ti);
                changed = true;
                this.pendingSync.add(ti);
                continue;
            }

            ctx.dest = ctx.wc.offset(EnumFacing.byIndex(ti.side));
            TileEntity be = ctx.world.getTileEntity(ctx.dest);
            if (be instanceof ITubeFlow) {
                ITubeFlow flow = (ITubeFlow) be;
                flow.addTubeItem(ti);
                continue;
            }
            if (be instanceof ITubeConnectable
                    && ((ITubeConnectable) be).tubeItemEnter((ti.side ^ 1) & 0x3F, ti.mode, ti)) {
                continue;
            }
            if (this.handleItem(ti, ctx)) {
                continue;
            }
            ti.progress = 0;
            ti.scheduled = false;
            ti.mode = 2;
            this.contents.add(ti);
            changed = true;
            this.pendingSync.add(ti);
        }
        return changed;
    }

    public void add(TubeItem ti) {
        ti.progress = 0;
        ti.lastProgress = -16;
        ti.scheduled = false;
        this.contents.add(ti);
    }

    public void onRemove() {
        TileEntity te = this.getParent();
        World world = te.getWorld();
        if (world == null) {
            return;
        }
        BlockPos pos = te.getPos();
        for (TubeItem ti : this.contents) {
            if (ti == null || ti.item.isEmpty()) {
                continue;
            }
            EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ti.item.copy());
            world.spawnEntity(entity);
        }
        this.contents.clear();
    }

    public void readFromNBT(NBTTagCompound tag) {
        this.contents = new LinkedList<>();
        NBTTagList items = tag.getTagList("Items", 10);
        for (int i = 0; i < items.tagCount(); ++i) {
            this.contents.add(TubeItem.newFromNBT(items.getCompoundTagAt(i)));
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList items = new NBTTagList();
        for (TubeItem ti : this.contents) {
            NBTTagCompound item = new NBTTagCompound();
            ti.writeToNBT(item);
            items.appendTag(item);
        }
        tag.setTag("Items", items);
    }

    public static class TubeScheduleContext {
        public final World world;
        public final BlockPos wc;
        public final int cons;
        public final List<TubeItem> tir = new ArrayList<>();
        public Iterator<TubeItem> tii;
        public BlockPos dest;

        public TubeScheduleContext(TileEntity te) {
            this.world = Objects.requireNonNull(te.getWorld());
            this.wc = te.getPos();
            this.cons = TubeLib.getConnections(this.world, this.wc);
        }
    }
}
