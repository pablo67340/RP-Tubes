package com.rptubes.power;

/**
 * Loader-neutral FE buffer behind the RP2 power blocks.
 * One RP2 Battery Box storage unit is {@link #FE_PER_UNIT} FE (1 FE = 10 RP2 power).
 */
public final class PowerBuffer {
    public static final int FE_PER_UNIT = 100;

    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;
    private final Runnable onChanged;
    private long stored;
    private Object adapter;
    private boolean extractEnabled = true;

    public PowerBuffer(long capacity, long maxReceive, long maxExtract, Runnable onChanged) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.onChanged = onChanged;
    }

    public long receive(long amount, boolean simulate) {
        long accepted = Math.min(Math.min(amount, this.maxReceive), this.capacity - this.stored);
        if (accepted <= 0) {
            return 0;
        }
        if (!simulate) {
            this.stored += accepted;
            this.onChanged.run();
        }
        return accepted;
    }

    public long extract(long amount, boolean simulate) {
        if (!this.extractEnabled) {
            return 0;
        }
        long taken = Math.min(Math.min(amount, this.maxExtract), this.stored);
        if (taken <= 0) {
            return 0;
        }
        if (!simulate) {
            this.stored -= taken;
            this.onChanged.run();
        }
        return taken;
    }

    /** Adds generated energy, ignoring the external receive limit. */
    public void generate(long amount) {
        long added = Math.min(amount, this.capacity - this.stored);
        if (added > 0) {
            this.stored += added;
            this.onChanged.run();
        }
    }

    /** Removes energy for internal use, ignoring the external extract limit. */
    public long drain(long amount) {
        long taken = Math.min(amount, this.stored);
        if (taken > 0) {
            this.stored -= taken;
            this.onChanged.run();
        }
        return taken;
    }

    public void markChanged() {
        this.onChanged.run();
    }

    /**
     * One loader energy wrapper per buffer, so transaction snapshots of the same
     * buffer never interleave across wrapper instances.
     */
    @SuppressWarnings("unchecked")
    public <T> T adapter(java.util.function.Function<PowerBuffer, T> factory) {
        if (this.adapter == null) {
            this.adapter = factory.apply(this);
        }
        return (T) this.adapter;
    }

    public long getStored() {
        return this.stored;
    }

    public void setStored(long stored) {
        this.stored = Math.max(0, Math.min(stored, this.capacity));
    }

    public long getCapacity() {
        return this.capacity;
    }

    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public boolean canExtract() {
        return this.maxExtract > 0 && this.extractEnabled;
    }

    public void setExtractEnabled(boolean enabled) {
        this.extractEnabled = enabled;
    }
}
