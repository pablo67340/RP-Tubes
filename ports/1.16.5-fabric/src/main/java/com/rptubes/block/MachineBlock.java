package com.rptubes.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

/** Shared facing + active visuals for tube machines. */
public abstract class MachineBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    protected MachineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // OG TileMachine.getFacing: Rotation = the direction the player is looking,
        // so the working/port face points AWAY and the display face points at you.
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
