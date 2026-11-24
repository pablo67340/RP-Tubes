package com.rptubes.block;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.blockentity.DropsContents;
import com.rptubes.network.GuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/** Battery Box. Metadata holds the charge level 0-8 that picks the side texture (OG 129 + n). */
public class BatteryBoxBlock extends Block {
    @SideOnly(Side.CLIENT) private IIcon topIcon;
    @SideOnly(Side.CLIENT) private IIcon bottomIcon;
    @SideOnly(Side.CLIENT) private IIcon[] sideIcons;

    public BatteryBoxBlock() {
        super(Material.rock);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.topIcon = reg.registerIcon("rptubes:battery_box_top");
        this.bottomIcon = reg.registerIcon("rptubes:battery_box_bottom");
        this.sideIcons = new IIcon[9];
        for (int i = 0; i < 9; i++) {
            this.sideIcons[i] = reg.registerIcon("rptubes:battery_box_side_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0) {
            return this.bottomIcon;
        }
        if (side == 1) {
            return this.topIcon;
        }
        return this.sideIcons[Math.max(0, Math.min(8, meta))];
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new BatteryBoxBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side,
                                    float hitX, float hitY, float hitZ) {
        if (!world.isRemote && world.getTileEntity(x, y, z) instanceof BatteryBoxBlockEntity) {
            player.openGui(RPTubes.instance, GuiHandler.GUI_BATTERY_BOX, world, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity be = world.getTileEntity(x, y, z);
        if (be instanceof DropsContents) {
            ((DropsContents) be).dropContents();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
