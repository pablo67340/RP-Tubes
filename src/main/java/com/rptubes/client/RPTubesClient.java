//? if fabric {
/*package com.rptubes.client;

import com.rptubes.network.ClientPacketHandler;
//? if <1.20.6 {
/^import com.rptubes.network.ModNetworking;
^///?}
import com.rptubes.network.TubeItemUpsertPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class RPTubesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.init();
        //? if <1.20.6 {
        /^ClientPlayNetworking.registerGlobalReceiver(ModNetworking.TUBE_UPSERT, (client, handler, buf, responseSender) -> {
            TubeItemUpsertPacket packet = TubeItemUpsertPacket.decode(buf);
            client.execute(() -> ClientPacketHandler.handleTubeUpsert(packet));
        });
        ^///?} else if <1.21.1 {
        /^ClientPlayNetworking.registerGlobalReceiver(TubeItemUpsertPacket.TYPE, (packet, context) ->
                context.client().execute(() -> ClientPacketHandler.handleTubeUpsert(packet)));
        ^///?} else {
        ClientPlayNetworking.registerGlobalReceiver(TubeItemUpsertPacket.TYPE, (payload, context) ->
                context.client().execute(() -> ClientPacketHandler.handleTubeUpsert(payload)));
        //?}
    }
}
*///?}
