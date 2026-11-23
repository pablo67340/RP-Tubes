package com.rptubes.block;

import com.rptubes.blockentity.AcceleratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A full block that carries items like a tube and speeds them up while it has power. */
public class AcceleratorBlock extends PneumaticTubeBlock {
    public AcceleratorBlock(Properties properties) {
        //? if fabric && >=26.1.2 {
        /*super(properties, AcceleratorBlockEntity::new);
        *///?} else {
        super(properties, AcceleratorBlockEntity::new, null);
        //?}
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}
