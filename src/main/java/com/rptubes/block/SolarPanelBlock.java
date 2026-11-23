package com.rptubes.block;

import com.rptubes.blockentity.SolarPanelBlockEntity;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Port of the RP2 Solar Panel: a quarter-height slab that needs a solid block below. */
public class SolarPanelBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 4, 16);

    public SolarPanelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    //? if <1.21.11 {
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    //?} else {
    /*protected BlockState updateShape(BlockState state, LevelReader level,
                                     net.minecraft.world.level.ScheduledTickAccess tickAccess, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState,
                                     net.minecraft.util.RandomSource random) {
    *///?}
        if (direction == Direction.DOWN && !this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        //? if forgelike && <1.21.11 {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.SOLAR_PANEL.get(), SolarPanelBlockEntity::serverTick);
        //?} else if <1.21.11 {
        /*return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.SOLAR_PANEL, SolarPanelBlockEntity::serverTick);
        *///?} else if fabric {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.SOLAR_PANEL, SolarPanelBlockEntity::serverTick);
        *///?} else {
        /*return level.isClientSide() ? null :
                createTickerHelper(type, ModBlockEntities.SOLAR_PANEL.get(), SolarPanelBlockEntity::serverTick);
        *///?}
    }

    //? if >=1.20.6 {
    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SolarPanelBlock::new);
    }
    //?}
}
