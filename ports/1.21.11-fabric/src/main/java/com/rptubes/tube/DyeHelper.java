package com.rptubes.tube;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
        if (stack.getItem() instanceof DyeItem dye) {
            return dye.getDyeColor().getId() + 1;
        }
        return -1;
    }

    public static byte readPaint(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        return data.copyTag().getByteOr("pCol", (byte) 0);
    }

    public static void writePaint(ItemStack stack, byte color) {
        CustomData existing = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = existing.copyTag();
        if (color <= 0) {
            tag.remove("pCol");
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
            return;
        }
        tag.putByte("pCol", color);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static InteractionResult tryUseOnTube(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(hit.getBlockPos());
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && held.isEmpty()) {
            if (!level.isClientSide()) {
                tube.tryPaint(0);
            }
            return InteractionResult.SUCCESS;
        }
        int paint = paintFrom(held);
        if (paint < 0) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
