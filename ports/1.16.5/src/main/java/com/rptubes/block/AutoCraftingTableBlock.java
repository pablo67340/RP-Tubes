package com.rptubes.block;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.fml.network.NetworkHooks;

/** Port of BuildCraft BlockAutoWorkbench — no facing, same texture layout as the original. */
public class AutoCraftingTableBlock extends Block {
    public AutoCraftingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, net.minecraft.world.IBlockReader world) {
        return new AutoCraftingTableBlockEntity();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player,
                                 Hand hand, BlockRayTraceResult hit) {
        if (!level.isClientSide) {
            TileEntity be = level.getBlockEntity(pos);
            if (be instanceof AutoCraftingTableBlockEntity && player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) player;
                AutoCraftingTableBlockEntity table = (AutoCraftingTableBlockEntity) be;
                NetworkHooks.openGui(sp, table, pos);
            }
        }
        return ActionResultType.sidedSuccess(level.isClientSide);
    }
}
