package com.rptubes.tube;

import com.rptubes.util.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

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
            if (stack != null && !com.rptubes.util.Stacks.isEmpty(stack)) {
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

    public void onRemove(TileEntity te) {
        if (this.buffer == null) {
            return;
        }
        World world = te.getWorldObj();
        if (world == null) {
            return;
        }
        BlockPos pos = new com.rptubes.util.BlockPos(te.xCoord, te.yCoord, te.zCoord);
        for (TubeItem ti : this.buffer) {
            if (ti == null || com.rptubes.util.Stacks.isEmpty(ti.item)) {
                continue;
            }
            world.spawnEntityInWorld(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ti.item.copy()));
        }
        this.buffer.clear();
    }

    public void readFromNBT(NBTTagCompound tag) {
        this.buffer = null;
        NBTTagList items = tag.getTagList("Buffer", 10);
        if (items.tagCount() > 0) {
            this.buffer = new LinkedList<>();
            for (int i = 0; i < items.tagCount(); i++) {
                this.buffer.add(TubeItem.newFromNBT(items.getCompoundTagAt(i)));
            }
        }
        this.plugged = tag.getByte("Plug") > 0;
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList items = new NBTTagList();
        if (this.buffer != null) {
            for (TubeItem ti : this.buffer) {
                NBTTagCompound item = new NBTTagCompound();
                ti.writeToNBT(item);
                items.appendTag(item);
            }
        }
        tag.setTag("Buffer", items);
        tag.setByte("Plug", (byte) (this.plugged ? 1 : 0));
    }
}
