package com.rptubes.network;

import com.rptubes.RPTubes;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class ModNetworking {
    public static final ResourceLocation TUBE_UPSERT = new ResourceLocation(RPTubes.MOD_ID, "tube_upsert");

    private ModNetworking() {
    }

    public static void register() {
    }

    public static void sendToTracking(Level level, ChunkPos chunkPos, TubeItemUpsertPacket packet) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        PlayerLookup.tracking(serverLevel, chunkPos).forEach(player ->
                ServerPlayNetworking.send(player, TUBE_UPSERT, packet.toBuf()));
    }
}
