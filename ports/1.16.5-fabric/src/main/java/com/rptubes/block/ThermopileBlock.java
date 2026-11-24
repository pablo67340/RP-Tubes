package com.rptubes.block;

import net.minecraft.world.level.block.EntityBlock;

import com.rptubes.blockentity.ThermopileBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class ThermopileBlock extends Block implements EntityBlock {
    public ThermopileBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter world) {
        return new ThermopileBlockEntity();
    }
}
