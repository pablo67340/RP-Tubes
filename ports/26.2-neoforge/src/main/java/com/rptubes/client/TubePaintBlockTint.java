package com.rptubes.client;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.PaintColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public enum TubePaintBlockTint implements BlockTintSource {
    INSTANCE;

    @Override
    public int color(BlockState state) {
        return ARGB.opaque(0xFFFFFF);
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof PneumaticTubeBlockEntity tube) {
            return ARGB.opaque(PaintColors.rgb(tube.getPaintColor()));
        }
        return ARGB.opaque(0xFFFFFF);
    }
}
