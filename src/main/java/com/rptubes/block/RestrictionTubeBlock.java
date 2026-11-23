package com.rptubes.block;

import com.rptubes.blockentity.RestrictionTubeBlockEntity;

public class RestrictionTubeBlock extends PneumaticTubeBlock {
    public RestrictionTubeBlock(Properties properties) {
        //? if fabric && >=26.1.2 {
        /*super(properties, RestrictionTubeBlockEntity::new);
        *///?} else {
        super(properties, RestrictionTubeBlockEntity::new, null);
        //?}
    }
}
