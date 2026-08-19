package com.rptubes.block;

import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;

public class BlockBreakerBlock extends MachineBlock {
    public BlockBreakerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new BlockBreakerBlockEntity();
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos,
                                net.minecraft.block.Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockBreakerBlockEntity) {
            BlockBreakerBlockEntity breaker = (BlockBreakerBlockEntity) be;
            breaker.onNeighborChange();
        }
    }
}
