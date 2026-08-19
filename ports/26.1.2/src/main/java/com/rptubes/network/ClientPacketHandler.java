package com.rptubes.network;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Client-only packet handling, kept separate so the server never classloads it. */
public class ClientPacketHandler {
    public static void handleTubeUpsert(TubeItemUpsertPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        BlockEntity be = level.getBlockEntity(msg.getPos());
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return;
        }
        for (int i = 0; i < msg.getItems().size(); i++) {
            tube.upsertItem(TubeItem.newFromNBT(msg.getItems().getCompoundOrEmpty(i), level.registryAccess()));
        }
    }
}
