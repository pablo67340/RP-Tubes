package com.rptubes.block;

import com.rptubes.blockentity.DeployerBlockEntity;
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

public class DeployerBlock extends MachineBlock {
    public DeployerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new DeployerBlockEntity();
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.item.BlockItemUseContext context) {
        // OG deployer works on Rotation^1 — its working face points AT the placer.
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player,
                                 Hand hand, BlockRayTraceResult hit) {
        if (!level.isClientSide) {
            TileEntity be = level.getBlockEntity(pos);
            if (be instanceof DeployerBlockEntity && player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) player;
                DeployerBlockEntity deployer = (DeployerBlockEntity) be;
                NetworkHooks.openGui(sp, deployer, pos);
            }
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos,
                                net.minecraft.block.Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof DeployerBlockEntity) {
            DeployerBlockEntity deployer = (DeployerBlockEntity) be;
            deployer.onNeighborChange();
        }
    }
}
