package com.rptubes.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(TubeItemUpsertPacket.TYPE, TubeItemUpsertPacket.STREAM_CODEC);
    }

    public static void sendToTracking(Level level, ChunkPos chunkPos, TubeItemUpsertPacket packet) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        // New packet instance per player so each send encodes a fresh buffer.
        PlayerLookup.tracking(serverLevel, chunkPos).forEach(player ->
                ServerPlayNetworking.send(player, new TubeItemUpsertPacket(packet.getPos(), packet.getItems())));
    }
}
