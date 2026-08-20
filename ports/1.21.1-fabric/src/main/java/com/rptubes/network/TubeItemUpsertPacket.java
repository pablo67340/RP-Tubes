package com.rptubes.network;

import com.rptubes.RPTubes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

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
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);

    public BlockPos getPos() {
        return this.pos;
    }

    public ListTag getItems() {
        return this.items;
    }

    private static void encode(RegistryFriendlyByteBuf buf, TubeItemUpsertPacket msg) {
        buf.writeBlockPos(msg.pos);
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("Items", msg.items);
        buf.writeNbt(wrapper);
    }

    private static TubeItemUpsertPacket decode(RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag wrapper = buf.readNbt();
        ListTag items = wrapper == null ? new ListTag() : wrapper.getList("Items", Tag.TAG_COMPOUND);
        return new TubeItemUpsertPacket(pos, items);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
