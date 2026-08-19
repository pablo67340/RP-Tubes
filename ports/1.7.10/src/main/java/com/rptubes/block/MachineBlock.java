package com.rptubes.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Shared facing + active visuals for tube machines.
 * Meta layout: bits 0-2 = facing (D,U,N,S,W,E), bit 3 = active.
 */
public abstract class MachineBlock extends Block {
    private final String frontTex;
    private final String backTex;
    private final String udTex;
    private final String weTex;
    private final String frontActiveTex;
    private final String udActiveTex;
    private final String weActiveTex;

    @SideOnly(Side.CLIENT) protected IIcon frontIcon;
    @SideOnly(Side.CLIENT) protected IIcon backIcon;
    @SideOnly(Side.CLIENT) protected IIcon udIcon;
    @SideOnly(Side.CLIENT) protected IIcon weIcon;
    @SideOnly(Side.CLIENT) protected IIcon frontActiveIcon;
    @SideOnly(Side.CLIENT) protected IIcon udActiveIcon;
    @SideOnly(Side.CLIENT) protected IIcon weActiveIcon;

    protected MachineBlock(String front, String back, String ud, String we,
                           String frontActive, String udActive, String weActive) {
        super(Material.rock);
        this.frontTex = front;
        this.backTex = back;
        this.udTex = ud;
        this.weTex = we;
        this.frontActiveTex = frontActive;
        this.udActiveTex = udActive;
        this.weActiveTex = weActive;
    }

    public static ForgeDirection getFacing(int meta) {
        return ForgeDirection.getOrientation(meta & 7);
    }

    public static boolean isActive(int meta) {
        return (meta & 8) != 0;
    }

    public static void setActive(World world, int x, int y, int z, boolean active) {
        int meta = world.getBlockMetadata(x, y, z);
        int next = active ? meta | 8 : meta & ~8;
        if (next != meta) {
            world.setBlockMetadataWithNotify(x, y, z, next, 2);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.frontIcon = reg.registerIcon("rptubes:" + this.frontTex);
        this.backIcon = reg.registerIcon("rptubes:" + this.backTex);
        this.udIcon = reg.registerIcon("rptubes:" + this.udTex);
        this.weIcon = reg.registerIcon("rptubes:" + this.weTex);
        this.frontActiveIcon = this.frontActiveTex == null ? null : reg.registerIcon("rptubes:" + this.frontActiveTex);
        this.udActiveIcon = this.udActiveTex == null ? null : reg.registerIcon("rptubes:" + this.udActiveTex);
        this.weActiveIcon = this.weActiveTex == null ? null : reg.registerIcon("rptubes:" + this.weActiveTex);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int facing = meta & 7;
        boolean active = (meta & 8) != 0;
        if (side == facing) {
            return active && this.frontActiveIcon != null ? this.frontActiveIcon : this.frontIcon;
        }
        if (side == (facing ^ 1)) {
            return this.backIcon;
        }
        if (side < 2) {
            return active && this.udActiveIcon != null ? this.udActiveIcon : this.udIcon;
        }
        return active && this.weActiveIcon != null ? this.weActiveIcon : this.weIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // OG TileMachine.getFacing: Rotation = the direction the player is looking,
        // so the working/port face points AWAY and the display face points at you.
        int toward = BlockPistonBase.determineOrientation(world, x, y, z, placer);
        world.setBlockMetadataWithNotify(x, y, z, toward ^ 1, 2);
    }
}
