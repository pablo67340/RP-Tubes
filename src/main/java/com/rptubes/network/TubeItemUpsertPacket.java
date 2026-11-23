package com.rptubes.network;

//? if fabric && <1.20.6 {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
*///?} else if !forge {
import com.rptubes.RPTubes;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
//? if fabric && <1.20.6 {
/*import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 ^/
public class TubeItemUpsertPacket {
    private final BlockPos pos;
    private final ListTag items;

    public TubeItemUpsertPacket(BlockPos pos, ListTag items) {
        this.pos = pos;
        this.items = items;
    }
*///?} else if fabric && <1.21.1 {
/*import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 * NBT format is unchanged from the Forge packet.
 ^/
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
*///?} else if neoforge && <1.21.1 {
/*import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 ^/
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(new ResourceLocation(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
*///?} else if fabric && <1.21.11 {
/*import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 ^/
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
*///?} else if neoforge && <1.21.11 {
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
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
//?} else if fabric && <26.1.2 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 ^/
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
*///?} else if neoforge && <26.1.2 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 ^/
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
*///?} else if neoforge && <26.2 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 ^/
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
*///?} else if neoforge {
/*import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 ^/
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<FriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
*///?} else if <1.20.6 {
/*import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 ^/
public class TubeItemUpsertPacket {
    private final BlockPos pos;
    private final ListTag items;

    public TubeItemUpsertPacket(BlockPos pos, ListTag items) {
        this.pos = pos;
        this.items = items;
    }
*///?} else if fabric {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 ^/
public record TubeItemUpsertPacket(BlockPos pos, ListTag items) implements CustomPacketPayload {
    public static final Type<TubeItemUpsertPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "tube_upsert"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TubeItemUpsertPacket> STREAM_CODEC =
            StreamCodec.of(TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode);
*///?} else if >=26.1.2 {
/*import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 ^/
public class TubeItemUpsertPacket {
    private final BlockPos pos;
    private final ListTag items;

    public TubeItemUpsertPacket(BlockPos pos, ListTag items) {
        this.pos = pos;
        this.items = items;
    }
*///?} else {
/*import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

/^*
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 ^/
public class TubeItemUpsertPacket {
    private final BlockPos pos;
    private final ListTag items;

    public TubeItemUpsertPacket(BlockPos pos, ListTag items) {
        this.pos = pos;
        this.items = items;
    }
*///?}

    public BlockPos getPos() {
        return this.pos;
    }

    public ListTag getItems() {
        return this.items;
    }

    //? if forge || <1.20.6 {
    /*public static void encode(TubeItemUpsertPacket msg, FriendlyByteBuf buf) {
    *///?} else if fabric && <1.21.1 {
    /*public static void encode(FriendlyByteBuf buf, TubeItemUpsertPacket msg) {
    *///?} else if neoforge && >=26.2 {
    /*private static void encode(FriendlyByteBuf buf, TubeItemUpsertPacket msg) {
    *///?} else {
    private static void encode(RegistryFriendlyByteBuf buf, TubeItemUpsertPacket msg) {
    //?}
        buf.writeBlockPos(msg.pos);
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("Items", msg.items);
        buf.writeNbt(wrapper);
    }

    //? if forge || (fabric && <1.21.1) {
    /*public static TubeItemUpsertPacket decode(FriendlyByteBuf buf) {
    *///?} else if neoforge && >=26.2 {
    /*private static TubeItemUpsertPacket decode(FriendlyByteBuf buf) {
    *///?} else {
    private static TubeItemUpsertPacket decode(RegistryFriendlyByteBuf buf) {
    //?}
        BlockPos pos = buf.readBlockPos();
        CompoundTag wrapper = buf.readNbt();
        //? if <1.21.11 {
        ListTag items = wrapper == null ? new ListTag() : wrapper.getList("Items", Tag.TAG_COMPOUND);
        //?} else {
        /*ListTag items = wrapper == null ? new ListTag() : wrapper.getListOrEmpty("Items");
        *///?}
        return new TubeItemUpsertPacket(pos, items);
    }

    //? if forge && <1.20.6 {
    /*public static void handle(TubeItemUpsertPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleTubeUpsert(msg)));
        ctx.get().setPacketHandled(true);
    }
    *///?} else if <1.20.6 {
    /*public FriendlyByteBuf toBuf() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        encode(this, buf);
        return buf;
    }
    *///?} else if fabric {
    /*@Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    *///?} else if neoforge {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TubeItemUpsertPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPacketHandler.handleTubeUpsert(msg));
    }
    //?} else if <26.1.2 {
    /*public static void handle(TubeItemUpsertPacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleTubeUpsert(msg)));
        ctx.setPacketHandled(true);
    }
    *///?} else {
    /*public static void handle(TubeItemUpsertPacket msg, CustomPayloadEvent.Context ctx) {
        // 26.1: DistExecutor was removed; guard directly. This packet is only
        // sent server->client, so the client-only class is safe to touch here.
        ctx.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientPacketHandler.handleTubeUpsert(msg);
            }
        });
        ctx.setPacketHandled(true);
    }
    *///?}
}
