package com.rptubes.block;

import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

/** Port of Tubestuff's ACT Mk II block — no facing, crafts continuously via ticker. */
public class AutoCraftingTableMk2Block extends Block {
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    private net.minecraft.util.IIcon topIcon;
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    private net.minecraft.util.IIcon bottomIcon;
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    private net.minecraft.util.IIcon sideIcon;

    public AutoCraftingTableMk2Block() {
        super(net.minecraft.block.material.Material.iron);
        this.setHardness(3.0F);
        this.setStepSound(soundTypeMetal);
    }

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister reg) {
        this.topIcon = reg.registerIcon("rptubes:act_mk2_top");
        this.bottomIcon = reg.registerIcon("rptubes:act_mk2_bottom");
        this.sideIcon = reg.registerIcon("rptubes:act_mk2_side");
    }

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public net.minecraft.util.IIcon getIcon(int side, int meta) {
        if (side == 1) return this.topIcon;
        if (side == 0) return this.bottomIcon;
        return this.sideIcon;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new AutoCraftingTableMk2BlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isRemote) {
            TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            if (be instanceof AutoCraftingTableMk2BlockEntity && player instanceof EntityPlayerMP) {
                EntityPlayerMP sp = (EntityPlayerMP) player;
                AutoCraftingTableMk2BlockEntity table = (AutoCraftingTableMk2BlockEntity) be;
                player.openGui(com.rptubes.RPTubes.instance, com.rptubes.network.GuiHandler.GUI_ACT_MK2, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be instanceof AutoCraftingTableMk2BlockEntity) {
            ((AutoCraftingTableMk2BlockEntity) be).dropContents();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
