package com.rptubes.block;

import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemDetectorBlock extends MachineBlock {
    public ItemDetectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new ItemDetectorBlockEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player,
                                 Hand hand, BlockRayTraceResult hit) {
        if (!level.isClientSide) {
            TileEntity be = level.getBlockEntity(pos);
            if (be instanceof ItemDetectorBlockEntity && player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) player;
                ItemDetectorBlockEntity detector = (ItemDetectorBlockEntity) be;
                NetworkHooks.openGui(sp, detector, pos);
            }
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof ItemDetectorBlockEntity && ((ItemDetectorBlockEntity) be).isEmitting()) {
            return 15;
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
        return 0;
    }
}
