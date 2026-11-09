package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

/** Port of TileRestrictTube — massive routing weight so items avoid this path. */
public class RestrictionTubeBlockEntity extends PneumaticTubeBlockEntity {
    public RestrictionTubeBlockEntity() {
        super(ModBlockEntities.RESTRICTION_TUBE.get());
    }

    @Override
    public int getTubeConClass() {
        int paint = this.getPaintColor() & 0xFF;
        return paint == 0 ? 17 : 18 + paint;
    }

    @Override
    public int tubeWeight(int side, int state) {
        return 1_000_000;
    }
}
