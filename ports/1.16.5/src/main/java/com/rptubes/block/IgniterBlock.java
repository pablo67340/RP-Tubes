package com.rptubes.block;

import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.IWorldReader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;

public class IgniterBlock extends MachineBlock {
    public IgniterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new IgniterBlockEntity();
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.item.BlockItemUseContext context) {
        // OG igniter works on Rotation^1 — its working face points AT the placer.
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos,
                                net.minecraft.block.Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof IgniterBlockEntity) {
            IgniterBlockEntity igniter = (IgniterBlockEntity) be;
            igniter.onNeighborChange();
        }
    }

    /** OG: when Rotation==0 (down) and Active, block is a fire source. */
    @Override
    public boolean isFireSource(BlockState state, IWorldReader level, BlockPos pos, Direction side) {
        return state.getValue(ACTIVE) && state.getValue(FACING) == Direction.DOWN;
    }
}
