package com.rptubes.network;

import com.rptubes.RPTubes;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public final class ModNetworking {
    public static final ResourceLocation TUBE_UPSERT = new ResourceLocation(RPTubes.MOD_ID, "tube_upsert");

    private ModNetworking() {
    }

    public static void register() {
    }

    public static void sendToTracking(Level level, BlockPos pos, TubeItemUpsertPacket packet) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        PlayerLookup.tracking((ServerLevel) level, pos).forEach(player ->
                ServerPlayNetworking.send(player, TUBE_UPSERT, packet.toBuf()));
    }
}
