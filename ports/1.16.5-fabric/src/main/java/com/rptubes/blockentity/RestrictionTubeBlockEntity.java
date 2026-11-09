package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Port of TileRestrictTube — massive routing weight so items avoid this path. */
public class RestrictionTubeBlockEntity extends PneumaticTubeBlockEntity {
    public RestrictionTubeBlockEntity() {
        super(ModBlockEntities.RESTRICTION_TUBE);
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
