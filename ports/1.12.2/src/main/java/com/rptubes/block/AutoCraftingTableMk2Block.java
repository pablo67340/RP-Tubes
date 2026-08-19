package com.rptubes.block;

import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

/** Port of Tubestuff's ACT Mk II block — no facing, crafts continuously via ticker. */
public class AutoCraftingTableMk2Block extends Block {
    public AutoCraftingTableMk2Block() {
        super(net.minecraft.block.material.Material.IRON);
        this.setHardness(3.0F);
        this.setSoundType(net.minecraft.block.SoundType.METAL);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AutoCraftingTableMk2BlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos);
            if (be instanceof AutoCraftingTableMk2BlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                AutoCraftingTableMk2BlockEntity table = (AutoCraftingTableMk2BlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_ACT_MK2, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof AutoCraftingTableMk2BlockEntity) {
            ((AutoCraftingTableMk2BlockEntity) be).dropContents();
        }
        super.breakBlock(world, pos, state);
    }
}
