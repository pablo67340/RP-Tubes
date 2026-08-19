package com.rptubes.tube;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.item.ItemStack;

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

    public void readFromNBT(CompoundNBT tag) {
        this.item = ItemStack.of(tag.getCompound("Item"));
        if (tag.contains("tid")) {
            this.id = tag.getInt("tid");
        }
        this.side = tag.getByte("side");
        this.progress = tag.getShort("pos");
        this.mode = tag.getByte("mode");
        this.color = tag.getByte("col");
        if (this.progress < 0) {
            this.scheduled = true;
            this.progress = (short) (-this.progress - 1);
        } else {
            this.scheduled = false;
        }
        this.power = (short) (tag.getByte("pow") & 0xFF);
        this.lastProgress = (short) (this.progress - 16);
    }

    public void writeToNBT(CompoundNBT tag) {
        tag.put("Item", this.item.save(new CompoundNBT()));
        tag.putInt("tid", this.id);
        tag.putByte("side", this.side);
        tag.putShort("pos", (short) (this.scheduled ? -this.progress - 1 : this.progress));
        tag.putByte("mode", this.mode);
        tag.putByte("col", this.color);
        tag.putByte("pow", (byte) this.power);
    }

    public static TubeItem newFromNBT(CompoundNBT tag) {
        TubeItem ti = new TubeItem();
        ti.readFromNBT(tag);
        return ti;
    }
}
