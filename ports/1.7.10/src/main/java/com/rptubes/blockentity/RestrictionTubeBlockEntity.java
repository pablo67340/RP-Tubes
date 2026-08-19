package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import com.rptubes.util.BlockPos;

/** Port of TileRestrictTube — massive routing weight so items avoid this path. */
public class RestrictionTubeBlockEntity extends PneumaticTubeBlockEntity {
    public RestrictionTubeBlockEntity() {
        super();
    }

    @Override
    public int tubeWeight(int side, int state) {
        return 1_000_000;
    }
}
