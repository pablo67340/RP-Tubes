package com.rptubes.block;

import com.rptubes.blockentity.MagTubeBlockEntity;

public class MagTubeBlock extends PneumaticTubeBlock {
    public MagTubeBlock(Properties properties) {
        super(properties, MagTubeBlockEntity::new);
    }
}
