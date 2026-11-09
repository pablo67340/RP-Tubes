package com.rptubes.network;

import com.rptubes.RPTubes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class ModNetworking {
    private ModNetworking() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(RPTubes.PROTOCOL);
        registrar.playToClient(TubeItemUpsertPacket.TYPE, TubeItemUpsertPacket.STREAM_CODEC,
                TubeItemUpsertPacket::handle);
    }
}
