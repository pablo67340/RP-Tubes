package com.rptubes.blockentity;

import com.rptubes.RPTubes;
import com.rptubes.network.TubeItemUpsertPacket;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeFlow;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeFlow;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Port of eloraam.machine.TileTube — routes items to the nearest valid destination.
 */
public class PneumaticTubeBlockEntity extends TileEntity implements net.minecraft.tileentity.ITickableTileEntity, ITubeFlow {
    private byte lastDir = 0;
    /** OG paintColor: 0 = unpainted, 1-16 = dye. Used for sorter color routing. */
    private byte paintColor = 0;

    private final TubeFlow flow = new TubeFlow() {
        @Override
        public TileEntity getParent() {
            return PneumaticTubeBlockEntity.this;
        }

        @Override
        public boolean schedule(TubeItem ti, TubeScheduleContext tsc) {
            ti.scheduled = true;
            ti.progress = 0;
            int available = tsc.cons & ~(1 << ti.side);
            if (available == 0) {
                return true;
            }
            if (Integer.bitCount(available) == 1) {
                ti.side = (byte) Integer.numberOfTrailingZeros(available);
                return true;
            }
            if (tsc.world.isClientSide) {
                // Can't route client-side at a junction; freeze the item at the
                // tube center until the server sync provides the chosen route.
                ti.side = (byte) -1;
                return true;
            }
            // Routing decision the client can't predict — send an item event
            this.pendingSync.add(ti);
            if (ti.mode != 3) {
                ti.mode = 1;
            }
            ti.side = (byte) TubeLib.findRoute(tsc.world, tsc.wc, ti, available, ti.mode, lastDir);
            if (ti.side >= 0) {
                int m = available & ~((2 << lastDir) - 1);
                if (m == 0) {
                    m = available;
                }
                lastDir = m == 0 ? 0 : (byte) Integer.numberOfTrailingZeros(m);
            } else {
                ti.side = (byte) TubeLib.findRoute(tsc.world, tsc.wc, ti, tsc.cons, 2, lastDir);
                if (ti.side >= 0) {
                    ti.mode = 2;
                    return true;
                }
                if (ti.mode == 3) {
                    ti.side = (byte) TubeLib.findRoute(tsc.world, tsc.wc, ti, tsc.cons, 1, lastDir);
                    ti.mode = 1;
                }
                if (ti.side < 0) {
                    ti.side = lastDir;
                    int m = available & ~((2 << lastDir) - 1);
                    if (m == 0) {
                        m = available;
                    }
                    lastDir = m == 0 ? 0 : (byte) Integer.numberOfTrailingZeros(m);
                }
            }
            return true;
        }

        @Override
        public boolean handleItem(TubeItem ti, TubeScheduleContext tsc) {
            Direction side = Direction.from3DDataValue(ti.side);
            BlockPos dest = tsc.wc.relative(side);
            ItemStack leftover = MachineLib.insert(tsc.world, dest, side.getOpposite(), ti.item, false);
            if (leftover.isEmpty()) {
                return true;
            }
            ti.item = leftover;
            return false;
        }
    };

    public PneumaticTubeBlockEntity() {
        this(ModBlockEntities.PNEUMATIC_TUBE.get());
    }

    protected PneumaticTubeBlockEntity(TileEntityType<?> type) {
        super(type);
    }

    public static void tick(World level, BlockPos pos, BlockState state, PneumaticTubeBlockEntity be) {
        if (be.flow.update()) {
            be.setChanged();
        }
        if (!level.isClientSide && !be.flow.pendingSync.isEmpty()) {
            // Drain queued item events (entries, junction routes, bounces) into
            // one small packet. Items are captured in their end-of-tick state,
            // after routing has run. Sent over our own channel — hybrid servers
            // (Arclight) drop vanilla same-state BE updates.
            net.minecraft.nbt.ListNBT items = new net.minecraft.nbt.ListNBT();
            java.util.Set<Integer> seen = new java.util.HashSet<>();
            for (TubeItem ti : be.flow.pendingSync) {
                if (!seen.add(ti.id)) {
                    continue;
                }
                CompoundNBT itemTag = new CompoundNBT();
                ti.writeToNBT(itemTag);
                items.add(itemTag);
            }
            be.flow.pendingSync.clear();
            RPTubes.CHANNEL.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)),
                    new TubeItemUpsertPacket(pos, items));
        }
    }

    @Override
    public int getTubeConnectableSides() {
        return 0x3F;
    }

    @Override
    public int getTubeConClass() {
        return this.paintColor & 0xFF;
    }

    public byte getPaintColor() {
        return this.paintColor;
    }

    /**
     * Dye or clear this tube. {@code color} is 0 (unpainted) or 1-16.
     * Returns false if the color did not change.
     */
    public boolean tryPaint(int color) {
        byte next = (byte) (color < 0 ? 0 : color > 16 ? 16 : color);
        if (this.paintColor == next) {
            return false;
        }
        this.paintColor = next;
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
        }
        return true;
    }

    @Override
    public boolean canRouteItems() {
        return true;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem item) {
        if (state != 0) {
            return false;
        }
        if (!this.colorAllows(item)) {
            return false;
        }
        item.side = (byte) side;
        this.flow.add(item);
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            // Item entered the network from a machine — event the client can't predict
            this.flow.pendingSync.add(item);
        }
        return true;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
        // OG TileTube: tubes are pass-through (state 0), never a destination.
        // Answering true for state 1 made route-finding always "succeed",
        // so machines kept re-ejecting items with nowhere to go.
        return state == 0 && this.colorAllows(item);
    }

    /** OG: painted items may only enter a tube that is unpainted or the same color. */
    protected boolean colorAllows(TubeItem item) {
        return item.color == 0 || this.paintColor == 0 || item.color == this.paintColor;
    }

    @Override
    public int tubeWeight(int side, int state) {
        return 0;
    }

    @Override
    public void addTubeItem(TubeItem item) {
        if (this.level != null && this.level.isClientSide) {
            // Guard against double-adds if a server event already placed it here
            for (TubeItem existing : this.flow.contents) {
                if (existing.id == item.id) {
                    return;
                }
            }
        }
        // Tube-to-tube handoff — deterministic, the client simulates it itself
        item.side = (byte) (item.side ^ 1);
        item.progress = 0;
        item.lastProgress = -16;
        item.scheduled = false;
        this.flow.contents.add(item);
        this.setChanged();
    }

    @Override
    public TubeFlow getTubeFlow() {
        return this.flow;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        this.flow.writeToNBT(tag);
        tag.putByte("lastDir", this.lastDir);
        tag.putByte("pCol", this.paintColor);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.flow.readFromNBT(tag);
        this.lastDir = tag.getByte("lastDir");
        this.paintColor = tag.getByte("pCol");
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager connection, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        if (tag == null) {
            return;
        }
        // Non-destructive: vanilla BE packets fire on unrelated block updates
        // (e.g. redstone tube power changes); replacing contents wholesale
        // would delete locally simulated items and cause visual glitches.
        this.paintColor = tag.getByte("pCol");
        net.minecraft.nbt.ListNBT items = tag.getList("Items", 10);
        for (int i = 0; i < items.size(); i++) {
            this.upsertItem(TubeItem.newFromNBT(items.getCompound(i)));
        }
    }

    /**
     * Merge a single server item event into the local simulation.
     *
     * The client fully simulates tube flow; the server only reports what the
     * client can't predict (entries, junction routes, bounces). If the client
     * already has the item and agrees on its path, the further-along position
     * wins so motion never jumps backwards. On disagreement (routed at a
     * junction, bounced) the server's path wins. If the item isn't here, any
     * stale copy in a neighboring tube is removed before adding it.
     */
    public void upsertItem(TubeItem inc) {
        for (TubeItem existing : this.flow.contents) {
            if (existing.id != inc.id) {
                continue;
            }
            existing.item = inc.item;
            existing.color = inc.color;
            existing.power = inc.power;
            existing.mode = inc.mode;
            if (existing.side == inc.side && existing.scheduled == inc.scheduled) {
                if (inc.progress > existing.progress) {
                    existing.progress = inc.progress;
                    existing.lastProgress = inc.lastProgress;
                }
            } else {
                existing.side = inc.side;
                existing.scheduled = inc.scheduled;
                existing.progress = inc.progress;
                existing.lastProgress = inc.lastProgress;
            }
            return;
        }
        if (this.level != null) {
            for (Direction d : Direction.values()) {
                TileEntity nbe = this.level.getBlockEntity(this.worldPosition.relative(d));
                if (nbe instanceof PneumaticTubeBlockEntity) {
                    PneumaticTubeBlockEntity neighbor = (PneumaticTubeBlockEntity) nbe;
                    neighbor.flow.contents.removeIf(t -> t.id == inc.id);
                }
            }
        }
        this.flow.contents.add(inc);
    }

    @Override
    public void setRemoved() {
        this.flow.onRemove();
        super.setRemoved();
    }

    @Override
    public void tick() {
        if (this.level == null) {
            return;
        }
        tick(this.level, this.worldPosition, this.getBlockState(), this);
    }
}
