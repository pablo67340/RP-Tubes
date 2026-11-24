package com.rptubes.blockentity;

/** RP2 MagTube: a tube that never slows items down, so Accelerator speed carries over any distance. */
public class MagTubeBlockEntity extends PneumaticTubeBlockEntity {
    @Override
    protected int itemSpeedLoss() {
        return 0;
    }
}
