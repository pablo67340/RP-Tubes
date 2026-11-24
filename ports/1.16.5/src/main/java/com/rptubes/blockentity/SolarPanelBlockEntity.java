package com.rptubes.blockentity;

import com.rptubes.power.PowerBlockEntity;
import com.rptubes.registry.ModBlockEntities;

/** Port of TileSolarPanel: 20 FE/t (RP2 applyDirect(2.0)) while the sky is visible in daytime. */
public class SolarPanelBlockEntity extends PowerBlockEntity {
    public static final long OUTPUT = 20;

    public SolarPanelBlockEntity() {
        super(ModBlockEntities.SOLAR_PANEL.get(), 1000, 0, 1000);
    }

    @Override
    protected void serverTick() {
        if (this.level.dimensionType().hasSkyLight() && this.level.isDay()
                && this.level.canSeeSky(this.worldPosition.above())) {
            this.buffer.generate(OUTPUT);
        }
        this.pushToNeighbours(Long.MAX_VALUE);
    }
}
