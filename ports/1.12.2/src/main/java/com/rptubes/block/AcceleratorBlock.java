package com.rptubes.block;

import com.rptubes.blockentity.AcceleratorBlockEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/** A full block that carries items like a tube and speeds them up while it has power. */
public class AcceleratorBlock extends PneumaticTubeBlock {
    public AcceleratorBlock() {
        super(AcceleratorBlockEntity::new);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
    }
}
