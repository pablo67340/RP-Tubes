package com.rptubes.block;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.network.GuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Alloy furnace. Meta layout: bits 0-1 = horizontal facing (S,W,N,E like
 * vanilla furnace horizontal index), bit 2 = lit.
 */
public class AlloyFurnaceBlock extends Block {
    // Horizontal index -> ForgeDirection ordinal: 0=S(3), 1=W(4), 2=N(2), 3=E(5)
    private static final int[] HORIZ_TO_SIDE = {3, 4, 2, 5};

    @SideOnly(Side.CLIENT) private IIcon topIcon;
    @SideOnly(Side.CLIENT) private IIcon frontIcon;
    @SideOnly(Side.CLIENT) private IIcon frontOnIcon;
    @SideOnly(Side.CLIENT) private IIcon sideIcon;

    public AlloyFurnaceBlock() {
        super(Material.rock);
    }

    public static boolean isLit(int meta) {
        return (meta & 4) != 0;
    }

    public static void setLit(World world, int x, int y, int z, boolean lit) {
        int meta = world.getBlockMetadata(x, y, z);
        int next = lit ? meta | 4 : meta & ~4;
        if (next != meta) {
            world.setBlockMetadataWithNotify(x, y, z, next, 2);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.topIcon = reg.registerIcon("rptubes:alloy_furnace_top");
        this.frontIcon = reg.registerIcon("rptubes:alloy_furnace_front");
        this.frontOnIcon = reg.registerIcon("rptubes:alloy_furnace_front_on");
        this.sideIcon = reg.registerIcon("rptubes:alloy_furnace_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) {
            return this.topIcon;
        }
        int front = HORIZ_TO_SIDE[meta & 3];
        if (side == front) {
            return (meta & 4) != 0 ? this.frontOnIcon : this.frontIcon;
        }
        return this.sideIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Front faces the player: same math as vanilla furnace, stored as horizontal index
        int look = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        // look: 0=S,1=W,2=N,3=E of player view; front should face player => opposite of look
        int facing = (look + 2) & 3;
        int meta = world.getBlockMetadata(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, (meta & 4) | facing, 2);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return isLit(world.getBlockMetadata(x, y, z)) ? 13 : 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(x, y, z);
            if (be instanceof AlloyFurnaceBlockEntity) {
                player.openGui(RPTubes.instance, GuiHandler.GUI_ALLOY_FURNACE, world, x, y, z);
            }
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new AlloyFurnaceBlockEntity();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity be = world.getTileEntity(x, y, z);
        if (be instanceof AlloyFurnaceBlockEntity) {
            ((AlloyFurnaceBlockEntity) be).dropContents();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
