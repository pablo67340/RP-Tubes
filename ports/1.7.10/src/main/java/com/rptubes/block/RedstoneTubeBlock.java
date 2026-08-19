package com.rptubes.block;

import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Pneumatic tube that also weakly conducts redstone along its connections.
 * Works like redstone dust: power is cached on the block entity and only
 * recomputed on neighbor changes, with a reentrancy guard so tubes never
 * recursively evaluate each other (OG used RedPowerLib.isSearching()).
 *
 * Meta stores the visual glow level, OG ps = 0..3 (tiles 68+ps / 72+ps).
 */
public class RedstoneTubeBlock extends PneumaticTubeBlock {
    /** False while a tube is scanning neighbors, so other tubes report 0. */
    private static boolean shouldSignal = true;

    @SideOnly(Side.CLIENT) private IIcon[] sideIcons;
    @SideOnly(Side.CLIENT) private IIcon[] endIcons;

    public RedstoneTubeBlock() {
        super("redstone_tube_side", "redstone_tube_end");
    }

    @Override
    protected TileEntity newTube() {
        return new RedstoneTubeBlockEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        this.sideIcons = new IIcon[] {
                this.sideIcon,
                reg.registerIcon("rptubes:redstone_tube_side_p1"),
                reg.registerIcon("rptubes:redstone_tube_side_p2"),
                reg.registerIcon("rptubes:redstone_tube_side_p3")
        };
        this.endIcons = new IIcon[] {
                this.endIcon,
                reg.registerIcon("rptubes:redstone_tube_end_p1"),
                reg.registerIcon("rptubes:redstone_tube_end_p2"),
                reg.registerIcon("rptubes:redstone_tube_end_p3")
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.sideIcons[meta & 3];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getSideIcon(int meta) {
        return this.sideIcons[meta & 3];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getEndIcon(int meta) {
        return this.endIcons[meta & 3];
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        if (!shouldSignal) {
            return 0;
        }
        TileEntity be = world.getTileEntity(x, y, z);
        return be instanceof RedstoneTubeBlockEntity ? ((RedstoneTubeBlockEntity) be).getPower() : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        // OG tubes power weakly only (isBlockWeakPoweringTo)
        return 0;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (!world.isRemote) {
            this.updatePower(world, x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        if (!world.isRemote) {
            this.updatePower(world, x, y, z);
        }
    }

    private void updatePower(World world, int x, int y, int z) {
        TileEntity be = world.getTileEntity(x, y, z);
        if (!(be instanceof RedstoneTubeBlockEntity)) {
            return;
        }
        RedstoneTubeBlockEntity tube = (RedstoneTubeBlockEntity) be;
        int target = calculateTargetPower(world, x, y, z);
        if (tube.getPower() != target) {
            tube.setPower(target);
            // OG glow step: ps = (PowerState + 84) / 85 over 0..255 -> 0..3 over 0..15
            int glow = target == 0 ? 0 : Math.min(3, (target + 4) / 5);
            if (world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) != glow) {
                world.setBlockMetadataWithNotify(x, y, z, glow, 2);
            }
            // Wakes adjacent tubes (propagation) and any listening machines
            world.notifyBlocksOfNeighborChange(x, y, z, this);
        }
    }

    private static int calculateTargetPower(World world, int x, int y, int z) {
        // Adjacent redstone tubes: cached power minus dust-like decay
        int wire = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int nx = x + dir.offsetX, ny = y + dir.offsetY, nz = z + dir.offsetZ;
            if (world.getBlock(nx, ny, nz) instanceof RedstoneTubeBlock
                    && world.getTileEntity(nx, ny, nz) instanceof RedstoneTubeBlockEntity) {
                RedstoneTubeBlockEntity t = (RedstoneTubeBlockEntity) world.getTileEntity(nx, ny, nz);
                wire = Math.max(wire, t.getPower() - 1);
            }
        }
        // External sources, with tubes muted so nothing recurses back into us
        int external = 0;
        shouldSignal = false;
        try {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int nx = x + dir.offsetX, ny = y + dir.offsetY, nz = z + dir.offsetZ;
                if (world.getBlock(nx, ny, nz) instanceof RedstoneTubeBlock) {
                    continue;
                }
                external = Math.max(external, world.getIndirectPowerLevelTo(nx, ny, nz, dir.ordinal()));
            }
        } finally {
            shouldSignal = true;
        }
        return Math.min(15, Math.max(external, wire));
    }
}
