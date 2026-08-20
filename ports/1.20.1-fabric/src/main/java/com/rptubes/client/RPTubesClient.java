package com.rptubes.client;

import com.rptubes.network.ClientPacketHandler;
import com.rptubes.network.ModNetworking;
import com.rptubes.network.TubeItemUpsertPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class RPTubesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.init();
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.TUBE_UPSERT, (client, handler, buf, responseSender) -> {
            TubeItemUpsertPacket packet = TubeItemUpsertPacket.decode(buf);
            client.execute(() -> ClientPacketHandler.handleTubeUpsert(packet));
        });
    }
}
