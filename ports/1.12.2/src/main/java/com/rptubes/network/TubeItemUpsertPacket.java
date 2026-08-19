package com.rptubes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

/**
 * Server → client item event: "this item is now in this tube, in this state."
 *
 * The client fully simulates tube flow locally (that's what makes movement
 * smooth), so the server only sends events for what the client cannot
 * predict: items entering the network, junction routing decisions, and
 * bounces. The client UPSERTS each item — it never wholesale replaces a
 * tube's contents mid-play, which is what caused items to teleport or vanish
 * when packets raced the local simulation.
 */
public class TubeItemUpsertPacket implements IMessage {
    private BlockPos pos;
    private NBTTagList items;

    public TubeItemUpsertPacket() {
    }

    public TubeItemUpsertPacket(BlockPos pos, NBTTagList items) {
        this.pos = pos;
        this.items = items;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public NBTTagList getItems() {
        return this.items;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(this.pos);
        NBTTagCompound wrapper = new NBTTagCompound();
        wrapper.setTag("Items", this.items);
        pb.writeCompoundTag(wrapper);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos = pb.readBlockPos();
        NBTTagCompound wrapper;
        try {
            wrapper = pb.readCompoundTag();
        } catch (IOException e) {
            wrapper = null;
        }
        this.items = wrapper == null ? new NBTTagList() : wrapper.getTagList("Items", 10);
    }

    public static class Handler implements IMessageHandler<TubeItemUpsertPacket, IMessage> {
        @Override
        public IMessage onMessage(TubeItemUpsertPacket msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> ClientPacketHandler.handleTubeUpsert(msg));
            return null;
        }
    }
}
