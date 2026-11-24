package com.rptubes.block;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.RetrievulatorBlockEntity;
import com.rptubes.network.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Tubestuff Retrievulator: emits a pulse out of its back face to fire the Retriever behind it. */
public class RetrievulatorBlock extends MachineBlock {
    public RetrievulatorBlock() {
        super("retrievulator_out", "retrievulator_in", "retrievulator_side", "retrievulator_side",
                null, "retrievulator_side_on", "retrievulator_side_on");
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new RetrievulatorBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side,
                                    float hitX, float hitY, float hitZ) {
        if (!world.isRemote && world.getTileEntity(x, y, z) instanceof RetrievulatorBlockEntity) {
            player.openGui(RPTubes.instance, GuiHandler.GUI_RETRIEVULATOR, world, x, y, z);
        }
        return true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    /** {@code side} points from the receiver toward this block, so the Retriever behind sees FACING. */
    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        return isActive(meta) && side == (meta & 7) ? 15 : 0;
    }
}
