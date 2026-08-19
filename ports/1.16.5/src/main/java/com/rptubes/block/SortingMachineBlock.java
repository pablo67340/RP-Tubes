package com.rptubes.block;

import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkHooks;

public class SortingMachineBlock extends MachineBlock {
    public SortingMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new SortingMachineBlockEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player,
                                 Hand hand, BlockRayTraceResult hit) {
        if (!level.isClientSide) {
            TileEntity be = level.getBlockEntity(pos);
            if (be instanceof SortingMachineBlockEntity && player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) player;
                SortingMachineBlockEntity sorter = (SortingMachineBlockEntity) be;
                NetworkHooks.openGui(sp, sorter, pos);
            }
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos,
                                net.minecraft.block.Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof SortingMachineBlockEntity) {
            SortingMachineBlockEntity sorter = (SortingMachineBlockEntity) be;
            sorter.onNeighborChange();
        }
    }
}
