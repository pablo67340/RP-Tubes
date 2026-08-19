package com.rptubes.block;

import com.rptubes.blockentity.BufferBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkHooks;

public class BufferBlock extends MachineBlock {
    public BufferBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new BufferBlockEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player,
                                 Hand hand, BlockRayTraceResult hit) {
        if (!level.isClientSide) {
            TileEntity be = level.getBlockEntity(pos);
            if (be instanceof BufferBlockEntity && player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) player;
                BufferBlockEntity buffer = (BufferBlockEntity) be;
                NetworkHooks.openGui(sp, buffer, pos);
            }
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }
}
