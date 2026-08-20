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
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → client item event: "this item is now in this tube, in this state."
 */
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(new ResourceLocation(RPTubes.MOD_ID, "tube_upsert"));

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

    public static void handle(TubeItemUpsertPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPacketHandler.handleTubeUpsert(msg));
    }
}
