package com.rptubes.tube;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Port of eloraam.core.TubeItem — an item currently traveling through tubes.
 */
public class TubeItem {
    public short progress = 0;
    /** Previous tick's progress, for client render interpolation. Not persisted. */
    public short lastProgress = -16;
    public byte mode = 1;
    public byte side;
    public byte color = 0;
    public short power = 0;
    public boolean scheduled = false;
    public ItemStack item = ItemStack.EMPTY;
    /**
     * Unique identity for client/server reconciliation. Matching by ItemStack
     * alone is ambiguous when several identical items are in flight at once
     * (rapid pulses), which made the client merge them and render fewer items.
     */
    public int id = java.util.concurrent.ThreadLocalRandom.current().nextInt();

    public TubeItem() {
    }

    public TubeItem(int side, ItemStack stack) {
        this.side = (byte) side;
        this.item = stack;
    }

    private static com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops(HolderLookup.Provider registries) {
        return registries != null
                ? registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE)
                : net.minecraft.nbt.NbtOps.INSTANCE;
    }

    public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        net.minecraft.nbt.Tag itemTag = tag.get("Item");
        this.item = itemTag == null ? ItemStack.EMPTY
                : ItemStack.OPTIONAL_CODEC.parse(ops(registries), itemTag).result().orElse(ItemStack.EMPTY);
        this.id = tag.getIntOr("tid", this.id);
        this.side = tag.getByteOr("side", (byte) 0);
        this.progress = tag.getShortOr("pos", (short) 0);
        this.mode = tag.getByteOr("mode", (byte) 0);
        this.color = tag.getByteOr("col", (byte) 0);
        if (this.progress < 0) {
            this.scheduled = true;
            this.progress = (short) (-this.progress - 1);
        } else {
            this.scheduled = false;
        }
        this.power = (short) (tag.getByteOr("pow", (byte) 0) & 0xFF);
        this.lastProgress = (short) (this.progress - 16);
    }

    public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
        ItemStack.OPTIONAL_CODEC.encodeStart(ops(registries), this.item)
                .result().ifPresent(t -> tag.put("Item", t));
        tag.putInt("tid", this.id);
        tag.putByte("side", this.side);
        tag.putShort("pos", (short) (this.scheduled ? -this.progress - 1 : this.progress));
        tag.putByte("mode", this.mode);
        tag.putByte("col", this.color);
        tag.putByte("pow", (byte) this.power);
    }

    public static TubeItem newFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        TubeItem ti = new TubeItem();
        ti.readFromNBT(tag, registries);
        return ti;
    }
}
