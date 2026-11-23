package com.rptubes.item;

import com.rptubes.RPTubes;
//? if neoforge && <1.21.1 {
/*import net.minecraft.core.registries.Registries;
*///?} else if neoforge && <1.21.11 {
import net.minecraft.core.registries.BuiltInRegistries;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if forgelike {
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
//?} else if >=1.20.6 && <1.21.11 {
/*import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
*///?} else {
/*import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if forgelike {
import net.minecraft.world.item.context.UseOnContext;
//?}
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//? if forge {
/*import net.minecraftforge.registries.ForgeRegistries;

/^*
 * Port of eloraam.base.ItemScrewdriver — 200 uses, rotates blocks on right click.
 * OG cycled dispenser-style blocks through all 6 sides and machines via IRotatable.
 ^/
public class ScrewdriverItem extends Item {
*///?} else if fabric {

/*/^*
 * Port of eloraam.base.ItemScrewdriver — 200 uses, rotates blocks on right click.
 * UseBlockCallback runs before Block.use so rotation wins over the machine GUI.
 ^/
public class ScrewdriverItem extends Item {
*///?} else if <1.21.11 {

/**
 * Port of eloraam.base.ItemScrewdriver — 200 uses, rotates blocks on right click.
 * OG cycled dispenser-style blocks through all 6 sides and machines via IRotatable.
 */
public class ScrewdriverItem extends Item {
//?} else {
/*import net.minecraft.core.registries.BuiltInRegistries;

/^*
 * Port of eloraam.base.ItemScrewdriver — 200 uses, rotates blocks on right click.
 * OG cycled dispenser-style blocks through all 6 sides and machines via IRotatable.
 ^/
public class ScrewdriverItem extends Item {
*///?}
    public ScrewdriverItem(Properties properties) {
        super(properties.durability(128));
    }

    //? if forgelike {
    /**
     * Forge pre-block hook (same one the OG used) — runs before Block.use,
     * so rotation wins over the machine GUI opening.
     */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
    //?} else {
    /*public static InteractionResult tryRotate(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand) {
    *///?}
        BlockState state = level.getBlockState(pos);
        BlockState rotated = rotate(state);
        if (rotated == null || rotated == state) {
            return InteractionResult.PASS;
        }
        //? if fabric && <1.20.6 {
        /*if (!level.isClientSide) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
        *///?} else if fabric && <1.21.1 {
        /*if (!level.isClientSide) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            stack.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND
                            ? EquipmentSlot.MAINHAND
                            : EquipmentSlot.OFFHAND);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
        *///?} else if fabric && <1.21.11 {
        /*if (!level.isClientSide) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            stack.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
        *///?} else if fabric && <26.1.2 {
        /*if (!level.isClientSide()) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            if (player != null) {
                stack.hurtAndBreak(1, player,
                        hand == InteractionHand.MAIN_HAND
                                ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                                : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
            }
        }
        return InteractionResult.SUCCESS;
        *///?} else if <1.20.6 {
        /*if (!level.isClientSide) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            if (context.getPlayer() != null) {
                context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
                        p -> p.broadcastBreakEvent(context.getHand()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
        *///?} else if fabric {
        /*if (!level.isClientSide()) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            stack.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND
                            ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                            : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        }
        return InteractionResult.SUCCESS;
        *///?} else if <1.21.11 {
        if (!level.isClientSide) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            if (context.getPlayer() != null) {
                context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
                        context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND
                                ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                                : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
        //?} else {
        /*if (!level.isClientSide()) {
            level.setBlock(pos, rotated, Block.UPDATE_ALL);
            if (context.getPlayer() != null) {
                context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
                        context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND
                                ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                                : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
            }
        }
        return InteractionResult.SUCCESS;
        *///?}
    }

    private static BlockState rotate(BlockState state) {
        //? if forge {
        /*// Only rotate this mod's own blocks, not chests/logs/other rotatables
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        *///?} else if fabric {
        /*ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        *///?} else if <1.21.1 {
        /*// Only rotate this mod's own blocks, not chests/logs/other rotatables
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
        *///?} else {
        // Only rotate this mod's own blocks, not chests/logs/other rotatables
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        //?}
        if (id == null || !id.getNamespace().equals(RPTubes.MOD_ID)) {
            return null;
        }
        //? if forgelike {
        // OG order for 6-way blocks: md 0..5 (D,U,N,S,W,E) which matches 3D data values
        if (state.hasProperty(BlockStateProperties.FACING)) {
        //?} else {
        /*if (state.hasProperty(BlockStateProperties.FACING)) {
        *///?}
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
