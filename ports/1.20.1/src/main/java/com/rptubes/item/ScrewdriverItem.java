package com.rptubes.item;

import com.rptubes.RPTubes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Port of eloraam.base.ItemScrewdriver — 200 uses, rotates blocks on right click.
 * OG cycled dispenser-style blocks through all 6 sides and machines via IRotatable.
 */
public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties properties) {
        super(properties.durability(128));
    }

    /**
     * Forge pre-block hook (same one the OG used) — runs before Block.use,
     * so rotation wins over the machine GUI opening.
     */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockState rotated = rotate(state);
        if (rotated == null || rotated == state) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            if (context.getPlayer() != null) {
                context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
                        p -> p.broadcastBreakEvent(context.getHand()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static BlockState rotate(BlockState state) {
        // Only rotate this mod's own blocks, not chests/logs/other rotatables
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (id == null || !id.getNamespace().equals(RPTubes.MOD_ID)) {
            return null;
        }
        // OG order for 6-way blocks: md 0..5 (D,U,N,S,W,E) which matches 3D data values
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
