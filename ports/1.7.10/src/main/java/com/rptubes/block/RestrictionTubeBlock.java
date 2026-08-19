package com.rptubes.block;

import com.rptubes.blockentity.RestrictionTubeBlockEntity;
import net.minecraft.tileentity.TileEntity;

public class RestrictionTubeBlock extends PneumaticTubeBlock {
    public RestrictionTubeBlock() {
        super("restriction_tube_side", "restriction_tube_end");
    }

    @Override
    protected TileEntity newTube() {
        return new RestrictionTubeBlockEntity();
    }
}
