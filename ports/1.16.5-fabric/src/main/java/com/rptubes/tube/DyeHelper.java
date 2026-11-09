package com.rptubes.tube;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

/** Resolve a held stack to a tube paint value (1-16), or -1 if it is not a dye. */
public final class DyeHelper {
    private DyeHelper() {
    }

    public static int paintFrom(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        if (stack.getItem() instanceof DyeItem) {
            DyeItem dye = (DyeItem) stack.getItem();
            return dye.getDyeColor().getId() + 1;
        }
        return -1;
    }

    public static byte readPaint(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getByte("pCol") : 0;
    }

    public static void writePaint(ItemStack stack, byte color) {
        if (color <= 0) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove("pCol");
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
            return;
        }
        stack.getOrCreateTag().putByte("pCol", color);
    }

    public static InteractionResult tryUseOnTube(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(hit.getBlockPos());
        if (!(be instanceof PneumaticTubeBlockEntity)) {
            return InteractionResult.PASS;
        }
        PneumaticTubeBlockEntity tube = (PneumaticTubeBlockEntity) be;
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && held.isEmpty()) {
            if (!level.isClientSide) {
                tube.tryPaint(0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        int paint = paintFrom(held);
        if (paint < 0) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && tube.tryPaint(paint) && !player.abilities.instabuild) {
            held.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
