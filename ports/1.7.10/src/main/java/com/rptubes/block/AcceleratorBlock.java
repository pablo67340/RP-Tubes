package com.rptubes.block;

import com.rptubes.blockentity.AcceleratorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/** A full block that carries items like a tube and speeds them up while it has power. */
public class AcceleratorBlock extends PneumaticTubeBlock {
    public AcceleratorBlock() {
        super("accelerator", "accelerator");
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    protected TileEntity newTube() {
        return new AcceleratorBlockEntity();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public int getRenderType() {
        return 0;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        if (mask.intersectsWith(box)) {
            list.add(box);
        }
    }
}
