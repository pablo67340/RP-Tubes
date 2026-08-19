package com.rptubes.block;

import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

public class BlockBreakerBlock extends MachineBlock {
    public BlockBreakerBlock() {
        super();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new BlockBreakerBlockEntity();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof BlockBreakerBlockEntity) {
            BlockBreakerBlockEntity breaker = (BlockBreakerBlockEntity) be;
            breaker.onNeighborChange();
        }
    }
}
