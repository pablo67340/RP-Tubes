package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;

/** RP2 MagTube: a tube that never slows items down, so Accelerator speed carries over any distance. */
public class MagTubeBlockEntity extends PneumaticTubeBlockEntity {
    public MagTubeBlockEntity() {
        super(ModBlockEntities.MAG_TUBE.get());
    }

    @Override
    protected int itemSpeedLoss() {
        return 0;
    }
}
