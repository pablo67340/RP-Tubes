package com.rptubes.tube;

//? if (forgelike && <1.21.11) || (neoforge && <26.1.2) {
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
//?} else if <1.20.6 {
/*import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
*///?} else if forgelike {
/*import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
*///?} else if <26.1.2 {
/*import com.rptubes.blockentity.PneumaticTubeBlockEntity;
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
*///?} else {
/*import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
*///?}

/** Resolve a held stack to a tube paint value (1-16), or -1 if it is not a dye. */
public final class DyeHelper {
    private DyeHelper() {
    }

    public static int paintFrom(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        //? if fabric && <1.20.6 {
        /*if (stack.getItem() instanceof DyeItem dye) {
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
        if (!(be instanceof PneumaticTubeBlockEntity tube)) {
            return InteractionResult.PASS;
        }
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
        if (!level.isClientSide && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
        *///?} else if fabric && >=1.21.11 && <26.1.2 {
        /*if (stack.getItem() instanceof DyeItem dye) {
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
        *///?} else if forge && >=1.21.11 && <26.1.2 {
        /*if (stack.getItem() instanceof DyeItem dye) {
            return dye.getDyeColor().getId() + 1;
        }
        DyeColor fromStack = DyeColor.getColor(stack);
        if (fromStack != null) {
            return fromStack.getId() + 1;
        }
        return -1;
    }
        *///?} else if fabric && <1.21.11 {
        /*if (stack.getItem() instanceof DyeItem dye) {
            return dye.getDyeColor().getId() + 1;
        }
        return -1;
    }

    public static byte readPaint(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        return data.copyTag().getByte("pCol");
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
            if (!level.isClientSide) {
                tube.tryPaint(0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        int paint = paintFrom(held);
        if (paint < 0) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && tube.tryPaint(paint) && !player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
        *///?} else if fabric {
        /*net.minecraft.world.item.DyeColor dye = stack.get(DataComponents.DYE);
        if (dye != null) {
            return dye.getId() + 1;
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
        *///?} else if forge && >=1.21.11 {
        /*if (stack.getItem() instanceof DyeItem) {
            DyeColor color = DyeColor.getColor(stack);
            if (color != null) {
                return color.getId() + 1;
            }
        }
        DyeColor fromStack = DyeColor.getColor(stack);
        return fromStack == null ? -1 : fromStack.getId() + 1;
    }
        *///?} else if >=26.1.2 {
        /*if (stack.getItem() instanceof DyeItem) {
            DyeColor color = DyeColor.getColor(stack);
            return color == null ? -1 : color.getId() + 1;
        }
        return -1;
    }
        *///?} else {
        if (stack.getItem() instanceof DyeItem dye) {
            return dye.getDyeColor().getId() + 1;
        }
        return -1;
    }
        //?}
}
