package com.rptubes.tube;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;

/** Port of eloraam.core.TubeBuffer */
public class TubeBuffer {
    private LinkedList<TubeItem> buffer;
    public boolean plugged = false;

    public boolean isEmpty() {
        return this.buffer == null || this.buffer.isEmpty();
    }

    public TubeItem getLast() {
        return this.isEmpty() ? null : this.buffer.getLast();
    }

    public void add(TubeItem ti) {
        if (this.buffer == null) {
            this.buffer = new LinkedList<>();
        }
        this.buffer.addFirst(ti);
    }

    public void addNew(ItemStack stack) {
        this.add(new TubeItem(0, stack));
    }

    public void addAll(Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack != null && !stack.isEmpty()) {
                this.addNew(stack.copy());
            }
        }
    }

    public void addNewColor(ItemStack stack, int color) {
        TubeItem ti = new TubeItem(0, stack);
        ti.color = (byte) color;
        this.add(ti);
    }

    public void addBounce(TubeItem ti) {
        if (this.buffer == null) {
            this.buffer = new LinkedList<>();
        }
        this.buffer.addLast(ti);
        this.plugged = true;
    }

    public void pop() {
        if (this.buffer == null) {
            return;
        }
        this.buffer.removeLast();
        if (this.buffer.isEmpty()) {
            this.plugged = false;
        }
    }

    public int size() {
        return this.buffer == null ? 0 : this.buffer.size();
    }

    public void onRemove(BlockEntity te) {
        if (this.buffer == null) {
            return;
        }
        Level level = te.getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = te.getBlockPos();
        for (TubeItem ti : this.buffer) {
            if (ti == null || ti.item.isEmpty()) {
                continue;
            }
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ti.item.copy()));
        }
        this.buffer.clear();
    }

    public void readFromNBT(CompoundTag tag) {
        this.buffer = null;
        ListTag items = tag.getList("Buffer", Tag.TAG_COMPOUND);
        if (!items.isEmpty()) {
            this.buffer = new LinkedList<>();
            for (int i = 0; i < items.size(); i++) {
                this.buffer.add(TubeItem.newFromNBT(items.getCompound(i)));
            }
        }
        this.plugged = tag.getByte("Plug") > 0;
    }

    public void writeToNBT(CompoundTag tag) {
        ListTag items = new ListTag();
        if (this.buffer != null) {
            for (TubeItem ti : this.buffer) {
                CompoundTag item = new CompoundTag();
                ti.writeToNBT(item);
                items.add(item);
            }
        }
        tag.put("Buffer", items);
        tag.putByte("Plug", (byte) (this.plugged ? 1 : 0));
    }
}
