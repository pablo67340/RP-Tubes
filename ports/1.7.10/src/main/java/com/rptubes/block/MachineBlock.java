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
    /** Icon per metadata and side, matching the rotated block models of the newer versions. */
    @SideOnly(Side.CLIENT) private IIcon[][] faceIcons;

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

    /**
     * Texture rotation of the model's up, down, west and east faces, copied from the block
     * model on the newer versions (front and back are never rotated).
     */
    protected int[] faceRotations() {
        return new int[]{0, 180, 270, 90};
    }

    /** False for blocks whose model stays the same whichever way they face. */
    protected boolean turnsWithFacing() {
        return true;
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

        // model faces in ForgeDirection order: down, up, north (front), south (back), west, east
        String ud = this.udTex;
        String we = this.weTex;
        String udOn = this.udActiveTex != null ? this.udActiveTex : ud;
        String weOn = this.weActiveTex != null ? this.weActiveTex : we;
        String frontOn = this.frontActiveTex != null ? this.frontActiveTex : this.frontTex;
        String[][] textures = {
                {ud, ud, this.frontTex, this.backTex, we, we},
                {udOn, udOn, frontOn, this.backTex, weOn, weOn}};
        int[] r = this.faceRotations();
        int[] rotation = {r[1], r[0], 0, 0, r[2], r[3]};
        this.faceIcons = new IIcon[16][6];
        for (int meta = 0; meta < 16; meta++) {
            int facing = this.turnsWithFacing() && (meta & 7) < 6 ? meta & 7 : 2;
            for (int side = 0; side < 6; side++) {
                int[] face = com.rptubes.client.BlockFaces.map(facing, side, rotation);
                this.faceIcons[meta][side] = com.rptubes.client.TransformedSprite.register(reg,
                        "rptubes:" + textures[(meta & 8) != 0 ? 1 : 0][face[0]], face[1]);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.faceIcons[meta & 15][side];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // OG TileMachine.getFacing: Rotation = the direction the player is looking,
        // so the working/port face points AWAY and the display face points at you.
        int toward = BlockPistonBase.determineOrientation(world, x, y, z, placer);
        world.setBlockMetadataWithNotify(x, y, z, toward ^ 1, 2);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        net.minecraft.tileentity.TileEntity be = world.getTileEntity(x, y, z);
        if (be instanceof com.rptubes.blockentity.DropsContents) {
            ((com.rptubes.blockentity.DropsContents) be).dropContents();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
