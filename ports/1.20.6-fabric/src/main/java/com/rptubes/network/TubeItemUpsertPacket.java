package com.rptubes.network;

import com.rptubes.RPTubes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server → client item event: "this item is now in this tube, in this state."
 * NBT format is unchanged from the Forge packet.
 */
public class TubeItemUpsertPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RPTubes.MOD_ID, "tube_upsert");
    public static final Type<TubeItemUpsertPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);

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

    public static void encode(FriendlyByteBuf buf, TubeItemUpsertPacket msg) {
        buf.writeBlockPos(msg.pos);
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("Items", msg.items);
        buf.writeNbt(wrapper);
    }

    public static TubeItemUpsertPacket decode(FriendlyByteBuf buf) {
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
