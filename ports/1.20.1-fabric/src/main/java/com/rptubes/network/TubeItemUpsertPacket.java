package com.rptubes.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Server → client item event: "this item is now in this tube, in this state."
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
        ListTag items = wrapper == null ? new ListTag() : wrapper.getList("Items", Tag.TAG_COMPOUND);
        return new TubeItemUpsertPacket(pos, items);
    }

    public FriendlyByteBuf toBuf() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        encode(this, buf);
        return buf;
    }
}
