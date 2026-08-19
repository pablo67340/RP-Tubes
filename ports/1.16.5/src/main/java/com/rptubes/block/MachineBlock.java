package com.rptubes.block;

import net.minecraft.util.Direction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;

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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // OG TileMachine.getFacing: Rotation = the direction the player is looking,
        // so the working/port face points AWAY and the display face points at you.
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
