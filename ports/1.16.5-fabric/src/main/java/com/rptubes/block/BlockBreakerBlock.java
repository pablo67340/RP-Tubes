package com.rptubes.block;

import net.minecraft.world.level.block.EntityBlock;

import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreakerBlock extends MachineBlock implements EntityBlock {
    public BlockBreakerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(net.minecraft.world.level.BlockGetter world) {
        return new BlockBreakerBlockEntity();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockBreakerBlockEntity) {
            BlockBreakerBlockEntity breaker = (BlockBreakerBlockEntity) be;
            breaker.onNeighborChange();
        }
    }
}
