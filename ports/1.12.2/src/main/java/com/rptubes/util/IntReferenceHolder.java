package com.rptubes.util;

/** Compat shim for the newer IntReferenceHolder/DataSlot API on 1.12. */
public abstract class IntReferenceHolder {
    private int prev = Integer.MIN_VALUE;

    public abstract int get();

    public abstract void set(int value);

    public boolean checkAndClearUpdateFlag() {
        int value = this.get();
        boolean changed = value != this.prev;
        this.prev = value;
        return changed;
    }
}
