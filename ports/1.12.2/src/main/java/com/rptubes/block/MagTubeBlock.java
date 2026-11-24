package com.rptubes.block;

import com.rptubes.blockentity.MagTubeBlockEntity;

public class MagTubeBlock extends PneumaticTubeBlock {
    public MagTubeBlock() {
        super(MagTubeBlockEntity::new);
    }
}
