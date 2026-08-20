package com.rptubes.block;

import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class IgniterBlock extends MachineBlock {
    public IgniterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IgniterBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        // OG igniter works on Rotation^1 — its working face points AT the placer.
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.IGNITER.get(), IgniterBlockEntity::serverTick);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IgniterBlockEntity igniter) {
            igniter.onNeighborChange();
        }
    }

    /** OG: when Rotation==0 (down) and Active, block is a fire source. */
    @Override
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        return state.getValue(ACTIVE) && state.getValue(FACING) == Direction.DOWN;
    }
}
