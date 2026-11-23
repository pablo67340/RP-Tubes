package com.rptubes.blockentity;

import com.rptubes.power.PowerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Port of TileSolarPanel: 20 FE/t (RP2 applyDirect(2.0)) while the sky is visible in daytime. */
public class SolarPanelBlockEntity extends PowerBlockEntity {
    public static final long OUTPUT = 20;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state, 1000, 0, 1000);
        //?} else {
        /*super(ModBlockEntities.SOLAR_PANEL, pos, state, 1000, 0, 1000);
        *///?}
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity be) {
        if (level.dimensionType().hasSkyLight() && isDay(level) && level.canSeeSky(pos.above())) {
            be.buffer.generate(OUTPUT);
        }
        be.pushToNeighbours(Long.MAX_VALUE);
    }

    private static boolean isDay(Level level) {
        //? if <1.21.11 {
        return level.isDay();
        //?} else {
        /*return level.isBrightOutside();
        *///?}
    }
}
