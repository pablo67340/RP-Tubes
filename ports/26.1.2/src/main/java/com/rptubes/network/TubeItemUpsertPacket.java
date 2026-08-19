package com.rptubes.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
    private final ListTag items;

    public TubeItemUpsertPacket(BlockPos pos, ListTag items) {
        this.pos = pos;
        this.items = items;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ListTag getItems() {
        return this.items;
    }

    public static void encode(TubeItemUpsertPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("Items", msg.items);
        buf.writeNbt(wrapper);
    }

    public static TubeItemUpsertPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag wrapper = buf.readNbt();
        ListTag items = wrapper == null ? new ListTag() : wrapper.getListOrEmpty("Items");
        return new TubeItemUpsertPacket(pos, items);
    }

    public static void handle(TubeItemUpsertPacket msg, CustomPayloadEvent.Context ctx) {
        // 26.1: DistExecutor was removed; guard directly. This packet is only
        // sent server->client, so the client-only class is safe to touch here.
        ctx.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientPacketHandler.handleTubeUpsert(msg);
            }
        });
        ctx.setPacketHandled(true);
    }
}
