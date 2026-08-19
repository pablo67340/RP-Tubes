package com.rptubes.block;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
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

/** Port of BuildCraft BlockAutoWorkbench — no facing, same texture layout as the original. */
public class AutoCraftingTableBlock extends Block {
    public AutoCraftingTableBlock() {
        super(net.minecraft.block.material.Material.WOOD);
        this.setHardness(2.0F);
        this.setSoundType(net.minecraft.block.SoundType.WOOD);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AutoCraftingTableBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos);
            if (be instanceof AutoCraftingTableBlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                AutoCraftingTableBlockEntity table = (AutoCraftingTableBlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_AUTO_CRAFTING_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof AutoCraftingTableBlockEntity) {
            ((AutoCraftingTableBlockEntity) be).dropContents();
        }
        super.breakBlock(world, pos, state);
    }
}
