package com.rptubes.block;

import com.rptubes.blockentity.AcceleratorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

/** A full block that carries items like a tube and speeds them up while it has power. */
public class AcceleratorBlock extends PneumaticTubeBlock {
    public AcceleratorBlock(Properties properties) {
        super(properties, AcceleratorBlockEntity::new);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.block();
    }
}
