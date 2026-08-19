package com.rptubes.tube;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.entity.item.ItemEntity;
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

        if (ctx.world.isClientSide) {
            // Hand items over to the next tube locally so movement is seamless;
            // the server sync packet corrects any divergence. Items entering
            // non-tube destinations simply vanish until the server confirms.
            for (TubeItem ti : ctx.tir) {
                if (ti.side < 0 || (ctx.cons & (1 << ti.side)) == 0) {
                    continue;
                }
                TileEntity be = ctx.world.getBlockEntity(ctx.wc.relative(Direction.from3DDataValue(ti.side)));
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
                    MachineLib.ejectItem(ctx.world, ctx.wc, ti.item, Direction.UP);
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

            ctx.dest = ctx.wc.relative(Direction.from3DDataValue(ti.side));
            TileEntity be = ctx.world.getBlockEntity(ctx.dest);
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
        World level = te.getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = te.getBlockPos();
        for (TubeItem ti : this.contents) {
            if (ti == null || ti.item.isEmpty()) {
                continue;
            }
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ti.item.copy());
            level.addFreshEntity(entity);
        }
        this.contents.clear();
    }

    public void readFromNBT(CompoundNBT tag) {
        this.contents = new LinkedList<>();
        ListNBT items = tag.getList("Items", 10);
        for (int i = 0; i < items.size(); ++i) {
            this.contents.add(TubeItem.newFromNBT(items.getCompound(i)));
        }
    }

    public void writeToNBT(CompoundNBT tag) {
        ListNBT items = new ListNBT();
        for (TubeItem ti : this.contents) {
            CompoundNBT item = new CompoundNBT();
            ti.writeToNBT(item);
            items.add(item);
        }
        tag.put("Items", items);
    }

    public static class TubeScheduleContext {
        public final World world;
        public final BlockPos wc;
        public final int cons;
        public final List<TubeItem> tir = new ArrayList<>();
        public Iterator<TubeItem> tii;
        public BlockPos dest;

        public TubeScheduleContext(TileEntity te) {
            this.world = Objects.requireNonNull(te.getLevel());
            this.wc = te.getBlockPos();
            this.cons = TubeLib.getConnections(this.world, this.wc);
        }
    }
}
