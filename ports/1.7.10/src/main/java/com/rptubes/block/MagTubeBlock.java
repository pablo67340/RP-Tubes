package com.rptubes.block;

import com.rptubes.blockentity.MagTubeBlockEntity;
import net.minecraft.tileentity.TileEntity;

/** RP2 MagTube: carries items without slowing them down. */
public class MagTubeBlock extends PneumaticTubeBlock {
    public MagTubeBlock() {
        super("mag_tube_side", "mag_tube_end");
    }

    @Override
    protected TileEntity newTube() {
        return new MagTubeBlockEntity();
    }
}
