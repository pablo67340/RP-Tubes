package com.rptubes.block;

import com.rptubes.blockentity.MagTubeBlockEntity;

public class MagTubeBlock extends PneumaticTubeBlock {
    public MagTubeBlock(Properties properties) {
        //? if fabric && >=26.1.2 {
        /*super(properties, MagTubeBlockEntity::new);
        *///?} else {
        super(properties, MagTubeBlockEntity::new, null);
        //?}
    }
}
