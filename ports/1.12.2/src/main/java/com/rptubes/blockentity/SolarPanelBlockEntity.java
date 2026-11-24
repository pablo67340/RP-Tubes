package com.rptubes.blockentity;

import com.rptubes.power.PowerBlockEntity;

/** Port of TileSolarPanel: 20 FE/t (RP2 applyDirect(2.0)) while the sky is visible in daytime. */
public class SolarPanelBlockEntity extends PowerBlockEntity {
    public static final long OUTPUT = 20;

    public SolarPanelBlockEntity() {
        super(1000, 0, 1000);
    }

    @Override
    protected void serverTick() {
        if (this.world.provider.hasSkyLight() && this.world.isDaytime()
                && this.world.canSeeSky(this.pos.up())) {
            this.buffer.generate(OUTPUT);
        }
        this.pushToNeighbours(Long.MAX_VALUE);
    }
}
