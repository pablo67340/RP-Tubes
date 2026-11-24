package com.rptubes.block;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.blockentity.DropsContents;
import com.rptubes.network.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BatteryBoxBlock extends Block {
    /** OG getStorageForRender: Storage * 8 / max, selecting side texture 129 + n. */
    public static final PropertyInteger CHARGE = PropertyInteger.create("charge", 0, 8);

    public BatteryBoxBlock() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CHARGE, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(CHARGE, Math.min(8, meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CHARGE);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new BatteryBoxBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof BatteryBoxBlockEntity) {
            player.openGui(RPTubes.instance, GuiHandler.GUI_BATTERY_BOX, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof DropsContents) {
            ((DropsContents) be).dropContents();
        }
        super.breakBlock(world, pos, state);
    }
}
