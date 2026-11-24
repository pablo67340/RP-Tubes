package com.rptubes.block;

import com.rptubes.blockentity.MagTubeBlockEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

/** RP2 MagTube: carries items without slowing them down. */
public class MagTubeBlock extends PneumaticTubeBlock {
    @SideOnly(Side.CLIENT) private IIcon coilIcon;
    @SideOnly(Side.CLIENT) private IIcon coilEndIcon;

    public MagTubeBlock() {
        super("mag_tube_side", "mag_tube_end");
    }

    @Override
    protected TileEntity newTube() {
        return new MagTubeBlockEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        this.coilIcon = reg.registerIcon("rptubes:mag_tube_coil");
        this.coilEndIcon = reg.registerIcon("rptubes:mag_tube_coil_end");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getCoilIcon() {
        return this.coilIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getCoilEndIcon() {
        return this.coilEndIcon;
    }
}
