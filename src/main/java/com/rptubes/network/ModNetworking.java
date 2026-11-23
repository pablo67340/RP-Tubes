//? if !forge {
package com.rptubes.network;

//? if <1.20.6 {
/*import com.rptubes.RPTubes;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class ModNetworking {
    public static final ResourceLocation TUBE_UPSERT = new ResourceLocation(RPTubes.MOD_ID, "tube_upsert");

*///?} else if fabric {
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class ModNetworking {
*///?} else if <1.21.1 {
/*import com.rptubes.RPTubes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = RPTubes.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModNetworking {
*///?} else {
import com.rptubes.RPTubes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class ModNetworking {
//?}
    private ModNetworking() {
    }

    //? if <1.20.6 {
    /*public static void register() {
    }

    public static void sendToTracking(Level level, ChunkPos chunkPos, TubeItemUpsertPacket packet) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        PlayerLookup.tracking(serverLevel, chunkPos).forEach(player ->
                ServerPlayNetworking.send(player, TUBE_UPSERT, packet.toBuf()));
    *///?} else if neoforge {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(RPTubes.PROTOCOL);
        registrar.playToClient(TubeItemUpsertPacket.TYPE, TubeItemUpsertPacket.STREAM_CODEC,
                TubeItemUpsertPacket::handle);
    //?} else if <1.21.1 {
    /*public static void register() {
        PayloadTypeRegistry.playS2C().register(TubeItemUpsertPacket.TYPE, TubeItemUpsertPacket.STREAM_CODEC);
    }

    public static void sendToTracking(Level level, ChunkPos chunkPos, TubeItemUpsertPacket packet) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        // New packet instance per player so each send encodes a fresh buffer.
        PlayerLookup.tracking(serverLevel, chunkPos).forEach(player ->
                ServerPlayNetworking.send(player, new TubeItemUpsertPacket(packet.getPos(), packet.getItems())));
    *///?} else if <26.1.2 {
    /*public static void register() {
        PayloadTypeRegistry.playS2C().register(TubeItemUpsertPacket.TYPE, TubeItemUpsertPacket.STREAM_CODEC);
    }

    public static void sendToTracking(Level level, ChunkPos chunkPos, TubeItemUpsertPacket packet) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        PlayerLookup.tracking(serverLevel, chunkPos).forEach(player ->
                ServerPlayNetworking.send(player, packet));
    *///?} else {
    /*public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(TubeItemUpsertPacket.TYPE, TubeItemUpsertPacket.STREAM_CODEC);
    }

    public static void sendToTracking(Level level, ChunkPos chunkPos, TubeItemUpsertPacket packet) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        PlayerLookup.tracking(serverLevel, chunkPos).forEach(player ->
                ServerPlayNetworking.send(player, packet));
    *///?}
    }
}
//?}
