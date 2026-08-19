package com.rptubes.block;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeLib;
import com.rptubes.util.BlockPos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Thin pneumatic tube. Connection mask mirrors RedPower TubeLib side bits.
 * On 1.7.10 connections are computed on the fly (no blockstates), and the
 * shape is rendered by TubeRenderer (ISBRH) as a core box plus arm boxes.
 */
public class PneumaticTubeBlock extends Block {
    /** Assigned by ClientSetup when the ISBRH registers. */
    public static int renderId = 0;

    // Match OG RenderTube doubleBox bounds: 0.25..0.75 => 4..12
    public static final float MIN = 0.25F;
    public static final float MAX = 0.75F;

    protected final String sideTex;
    protected final String endTex;

    @SideOnly(Side.CLIENT) protected IIcon sideIcon;
    @SideOnly(Side.CLIENT) protected IIcon endIcon;

    public PneumaticTubeBlock() {
        this("pneumatic_tube_side", "pneumatic_tube_end");
    }

    protected PneumaticTubeBlock(String sideTex, String endTex) {
        super(Material.iron);
        this.sideTex = sideTex;
        this.endTex = endTex;
        this.setBlockBounds(MIN, MIN, MIN, MAX, MAX, MAX);
    }

    protected TileEntity newTube() {
        return new PneumaticTubeBlockEntity();
    }

    /** Bitmask of connected sides, ForgeDirection ordinals. */
    public static int getConnectionMask(World world, int x, int y, int z) {
        int mask = 0;
        BlockPos pos = new BlockPos(x, y, z);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (TubeLib.canConnect(world, pos, dir)) {
                mask |= 1 << dir.ordinal();
            }
        }
        return mask;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.sideIcon = reg.registerIcon("rptubes:" + this.sideTex);
        this.endIcon = reg.registerIcon("rptubes:" + this.endTex);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.sideIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getSideIcon(int meta) {
        return this.sideIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getEndIcon(int meta) {
        return this.endIcon;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        float x1 = MIN, y1 = MIN, z1 = MIN, x2 = MAX, y2 = MAX, z2 = MAX;
        if (access instanceof World) {
            int mask = getConnectionMask((World) access, x, y, z);
            if ((mask & (1 << 0)) != 0) y1 = 0.0F;
            if ((mask & (1 << 1)) != 0) y2 = 1.0F;
            if ((mask & (1 << 2)) != 0) z1 = 0.0F;
            if ((mask & (1 << 3)) != 0) z2 = 1.0F;
            if ((mask & (1 << 4)) != 0) x1 = 0.0F;
            if ((mask & (1 << 5)) != 0) x2 = 1.0F;
        }
        this.setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
                                        @SuppressWarnings("rawtypes") List list, Entity entity) {
        int cons = getConnectionMask(world, x, y, z);
        addBox(world, x, y, z, mask, list, MIN, MIN, MIN, MAX, MAX, MAX);
        if ((cons & (1 << 0)) != 0) addBox(world, x, y, z, mask, list, MIN, 0, MIN, MAX, MIN, MAX);
        if ((cons & (1 << 1)) != 0) addBox(world, x, y, z, mask, list, MIN, MAX, MIN, MAX, 1, MAX);
        if ((cons & (1 << 2)) != 0) addBox(world, x, y, z, mask, list, MIN, MIN, 0, MAX, MAX, MIN);
        if ((cons & (1 << 3)) != 0) addBox(world, x, y, z, mask, list, MIN, MIN, MAX, MAX, MAX, 1);
        if ((cons & (1 << 4)) != 0) addBox(world, x, y, z, mask, list, 0, MIN, MIN, MIN, MAX, MAX);
        if ((cons & (1 << 5)) != 0) addBox(world, x, y, z, mask, list, MAX, MIN, MIN, 1, MAX, MAX);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addBox(World world, int x, int y, int z, AxisAlignedBB mask, List list,
                               double x1, double y1, double z1, double x2, double y2, double z2) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
        if (mask.intersectsWith(box)) {
            list.add(box);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!world.isRemote) {
            // connections are computed on the fly; just re-render
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return this.newTube();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity be = world.getTileEntity(x, y, z);
        if (be instanceof PneumaticTubeBlockEntity) {
            ((PneumaticTubeBlockEntity) be).dropContents();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
