package com.rptubes.item;

import com.rptubes.RPTubes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Port of eloraam.base.ItemScrewdriver — 200 uses, rotates blocks on right click.
 * UseBlockCallback runs before Block.use so rotation wins over the machine GUI.
 */
public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties properties) {
        super(properties.durability(128));
    }

    public static InteractionResult tryRotate(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand) {
        BlockState state = level.getBlockState(pos);
        BlockState rotated = rotate(state);
        if (rotated == null || rotated == state) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            stack.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static BlockState rotate(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id == null || !id.getNamespace().equals(RPTubes.MOD_ID)) {
            return null;
        }
        if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction next = Direction.from3DDataValue(
                    (state.getValue(BlockStateProperties.FACING).get3DDataValue() + 1) % 6);
            return state.setValue(BlockStateProperties.FACING, next);
        }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                    state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
        }
        return null;
    }
}
