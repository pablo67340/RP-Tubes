package com.rptubes.block;

import com.rptubes.blockentity.RestrictionTubeBlockEntity;

public class RestrictionTubeBlock extends PneumaticTubeBlock {
    public RestrictionTubeBlock() {
        super(RestrictionTubeBlockEntity::new);
    }
}
