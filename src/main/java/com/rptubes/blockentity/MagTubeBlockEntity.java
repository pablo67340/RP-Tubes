package com.rptubes.blockentity;

import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Port of TileMagTube: a tube that keeps the speed an Accelerator gives its items. */
public class MagTubeBlockEntity extends PneumaticTubeBlockEntity {
    public MagTubeBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.MAG_TUBE.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.MAG_TUBE, pos, state);
        *///?}
    }

    @Override
    protected int itemSpeedLoss() {
        return 0;
    }
}
