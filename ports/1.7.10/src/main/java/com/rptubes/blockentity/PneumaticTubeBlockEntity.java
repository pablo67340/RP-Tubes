package com.rptubes.blockentity;

import com.rptubes.RPTubes;
import com.rptubes.network.TubeItemUpsertPacket;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeFlow;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeFlow;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * Port of eloraam.machine.TileTube — routes items to the nearest valid destination.
 */
public class PneumaticTubeBlockEntity extends CompatTileEntity implements ITubeFlow {
    private byte lastDir = 0;

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
            if (tsc.world.isRemote) {
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
            ForgeDirection side = ForgeDirection.getOrientation(ti.side);
            BlockPos dest = tsc.wc.offset(side);
            ItemStack leftover = MachineLib.insert(tsc.world, dest, side.getOpposite(), ti.item, false);
            if (com.rptubes.util.Stacks.isEmpty(leftover)) {
                return true;
            }
            ti.item = leftover;
            return false;
        }
    };

    public PneumaticTubeBlockEntity() {
        super();
    }

    public static void tick(World world, BlockPos pos, PneumaticTubeBlockEntity be) {
        if (be.flow.update()) {
            be.markDirty();
        }
        if (!world.isRemote && !be.flow.pendingSync.isEmpty()) {
            // Drain queued item events (entries, junction routes, bounces) into
            // one small packet. Items are captured in their end-of-tick state,
            // after routing has run. Sent over our own channel — hybrid servers
            // (Arclight) drop vanilla same-state BE updates.
            net.minecraft.nbt.NBTTagList items = new net.minecraft.nbt.NBTTagList();
            java.util.Set<Integer> seen = new java.util.HashSet<>();
            for (TubeItem ti : be.flow.pendingSync) {
                if (!seen.add(ti.id)) {
                    continue;
                }
                NBTTagCompound itemTag = new NBTTagCompound();
                ti.writeToNBT(itemTag);
                items.appendTag(itemTag);
            }
            be.flow.pendingSync.clear();
            RPTubes.CHANNEL.sendToAllAround(new TubeItemUpsertPacket(pos, items),
                    new NetworkRegistry.TargetPoint(world.provider.dimensionId,
                            pos.getX(), pos.getY(), pos.getZ(), 64.0));
        }
    }

    @Override
    public int getTubeConnectableSides() {
        return 0x3F;
    }

    @Override
    public int getTubeConClass() {
        return 0;
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
        item.side = (byte) side;
        this.flow.add(item);
        this.markDirty();
        if (this.getWorld() != null && !this.getWorld().isRemote) {
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
        return state == 0;
    }

    @Override
    public int tubeWeight(int side, int state) {
        return 0;
    }

    @Override
    public void addTubeItem(TubeItem item) {
        if (this.getWorld() != null && this.getWorld().isRemote) {
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
        this.markDirty();
    }

    @Override
    public TubeFlow getTubeFlow() {
        return this.flow;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.flow.writeToNBT(tag);
        tag.setByte("lastDir", this.lastDir);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.flow.readFromNBT(tag);
        this.lastDir = tag.getByte("lastDir");
    }

    @Override
    public net.minecraft.network.Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, -1, tag);
    }

    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager connection, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.func_148857_g();
        if (tag == null) {
            return;
        }
        // Non-destructive: vanilla BE packets fire on unrelated block updates
        // (e.g. redstone tube power changes); replacing contents wholesale
        // would delete locally simulated items and cause visual glitches.
        net.minecraft.nbt.NBTTagList items = tag.getTagList("Items", 10);
        for (int i = 0; i < items.tagCount(); i++) {
            this.upsertItem(TubeItem.newFromNBT(items.getCompoundTagAt(i)));
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
        if (this.getWorld() != null) {
            for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
                BlockPos np = this.getPos().offset(d);
                TileEntity nbe = this.getWorld().getTileEntity(np.getX(), np.getY(), np.getZ());
                if (nbe instanceof PneumaticTubeBlockEntity) {
                    PneumaticTubeBlockEntity neighbor = (PneumaticTubeBlockEntity) nbe;
                    neighbor.flow.contents.removeIf(t -> t.id == inc.id);
                }
            }
        }
        this.flow.contents.add(inc);
    }

    public void dropContents() {
        this.flow.onRemove();
    }

    @Override
    public void updateEntity() {
        if (this.getWorld() == null) {
            return;
        }
        tick(this.getWorld(), this.getPos(), this);
    }
}
