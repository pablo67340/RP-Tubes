package com.rptubes.block;

import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

public class RegulatorBlock extends MachineBlock {
    public RegulatorBlock() {
        super();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new RegulatorBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos);
            if (be instanceof RegulatorBlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                RegulatorBlockEntity regulator = (RegulatorBlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_REGULATOR, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof RegulatorBlockEntity) {
            RegulatorBlockEntity regulator = (RegulatorBlockEntity) be;
            regulator.onNeighborChange();
        }
    }
}
