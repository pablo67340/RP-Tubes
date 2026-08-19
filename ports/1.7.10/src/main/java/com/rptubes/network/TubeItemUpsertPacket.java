package com.rptubes.network;

import com.rptubes.util.BlockPos;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

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
        pb.writeInt(this.pos.getX());
        pb.writeInt(this.pos.getY());
        pb.writeInt(this.pos.getZ());
        NBTTagCompound wrapper = new NBTTagCompound();
        wrapper.setTag("Items", this.items);
        try {
            pb.writeNBTTagCompoundToBuffer(wrapper);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.pos = new BlockPos(pb.readInt(), pb.readInt(), pb.readInt());
        NBTTagCompound wrapper;
        try {
            wrapper = pb.readNBTTagCompoundFromBuffer();
        } catch (IOException e) {
            wrapper = null;
        }
        this.items = wrapper == null ? new NBTTagList() : wrapper.getTagList("Items", 10);
    }

    public static class Handler implements IMessageHandler<TubeItemUpsertPacket, IMessage> {
        @Override
        public IMessage onMessage(final TubeItemUpsertPacket msg, MessageContext ctx) {
            // func_152344_a == addScheduledTask on 1.7.10: hop to the client thread
            Minecraft.getMinecraft().func_152344_a(new Runnable() {
                @Override
                public void run() {
                    ClientPacketHandler.handleTubeUpsert(msg);
                }
            });
            return null;
        }
    }
}
