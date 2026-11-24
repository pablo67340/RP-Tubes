package com.rptubes.block;

import com.rptubes.blockentity.ThermopileBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/** Port of the RP2 Thermopile: makes power from the temperature difference around it. */
public class ThermopileBlock extends MachineBlock {
    public ThermopileBlock() {
        super("thermopile_front", "thermopile_front", "thermopile_top", "thermopile_side", null, null, null);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new ThermopileBlockEntity();
    }
}
