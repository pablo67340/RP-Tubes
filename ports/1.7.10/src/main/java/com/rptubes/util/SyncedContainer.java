package com.rptubes.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.7.10 Container base that reimplements the newer data-slot sync API on
 * top of sendProgressBarUpdate/updateProgressBar, so menus keep their structure.
 */
public abstract class SyncedContainer extends Container {
    private final List<IntReferenceHolder> dataSlots = new ArrayList<IntReferenceHolder>();

    protected IntReferenceHolder addDataSlot(IntReferenceHolder holder) {
        this.dataSlots.add(holder);
        return holder;
    }

    protected void addDataSlots(final IIntArray array) {
        for (int i = 0; i < array.getCount(); i++) {
            final int idx = i;
            this.addDataSlot(new IntReferenceHolder() {
                @Override
                public int get() {
                    return array.get(idx);
                }

                @Override
                public void set(int value) {
                    array.set(idx, value);
                }
            });
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.dataSlots.size(); i++) {
            IntReferenceHolder holder = this.dataSlots.get(i);
            if (holder.checkAndClearUpdateFlag()) {
                for (Object listener : this.crafters) {
                    ((ICrafting) listener).sendProgressBarUpdate(this, i, holder.get());
                }
            }
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting listener) {
        super.addCraftingToCrafters(listener);
        for (int i = 0; i < this.dataSlots.size(); i++) {
            listener.sendProgressBarUpdate(this, i, this.dataSlots.get(i).get());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id >= 0 && id < this.dataSlots.size()) {
            this.dataSlots.get(id).set(data);
        }
    }
}
