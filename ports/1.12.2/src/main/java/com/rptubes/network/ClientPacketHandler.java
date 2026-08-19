package com.rptubes.network;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;

/** Client-only packet handling, kept separate so the server never classloads it. */
public class ClientPacketHandler {
    public static void handleTubeUpsert(TubeItemUpsertPacket msg) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) {
            return;
        }
        TileEntity be = world.getTileEntity(msg.getPos());
        if (!(be instanceof PneumaticTubeBlockEntity)) {
            return;
        }
        PneumaticTubeBlockEntity tube = (PneumaticTubeBlockEntity) be;
        for (int i = 0; i < msg.getItems().tagCount(); i++) {
            tube.upsertItem(TubeItem.newFromNBT(msg.getItems().getCompoundTagAt(i)));
        }
    }
}
