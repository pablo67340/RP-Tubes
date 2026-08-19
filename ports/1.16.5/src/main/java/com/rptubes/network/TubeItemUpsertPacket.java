package com.rptubes.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 */
public class TubeItemUpsertPacket {
    private final BlockPos pos;
    private final ListNBT items;

    public TubeItemUpsertPacket(BlockPos pos, ListNBT items) {
        this.pos = pos;
        this.items = items;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ListNBT getItems() {
        return this.items;
    }

    public static void encode(TubeItemUpsertPacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        CompoundNBT wrapper = new CompoundNBT();
        wrapper.put("Items", msg.items);
        buf.writeNbt(wrapper);
    }

    public static TubeItemUpsertPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundNBT wrapper = buf.readNbt();
        ListNBT items = wrapper == null ? new ListNBT() : wrapper.getList("Items", 10);
        return new TubeItemUpsertPacket(pos, items);
    }

    public static void handle(TubeItemUpsertPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleTubeUpsert(msg)));
        ctx.get().setPacketHandled(true);
    }
}
