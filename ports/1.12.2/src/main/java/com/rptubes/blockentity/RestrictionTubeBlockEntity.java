package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;

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
