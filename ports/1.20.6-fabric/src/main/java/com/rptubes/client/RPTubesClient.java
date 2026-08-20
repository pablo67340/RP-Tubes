package com.rptubes.client;

import com.rptubes.network.ClientPacketHandler;
import com.rptubes.network.TubeItemUpsertPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class RPTubesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.init();
        ClientPlayNetworking.registerGlobalReceiver(TubeItemUpsertPacket.TYPE, (packet, context) ->
                context.client().execute(() -> ClientPacketHandler.handleTubeUpsert(packet)));
    }
}
