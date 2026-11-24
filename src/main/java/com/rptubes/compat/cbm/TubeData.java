//? if cbm {
package com.rptubes.compat.cbm;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

/** Tube block entity data and item paint, in the form each Minecraft version uses. */
final class TubeData {
    private TubeData() {
    }

    static CompoundTag save(PneumaticTubeBlockEntity tube) {
        //? if <1.20.5 {
        /*return tube.saveWithoutMetadata();
        *///?} else {
        return tube.saveWithoutMetadata(tube.getLevel().registryAccess());
        //?}
    }

    static void load(PneumaticTubeBlockEntity tube, CompoundTag tag) {
        //? if <1.20.5 {
        /*tube.load(tag);
        *///?} else if <1.21.11 {
        tube.loadWithComponents(tag, tube.getLevel().registryAccess());
        //?} else {
        /*tube.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(
                net.minecraft.util.ProblemReporter.DISCARDING, tube.getLevel().registryAccess(), tag));
        *///?}
    }

    static byte paint(CompoundTag tag) {
        //? if <1.21.11 {
        return tag.getByte("pCol");
        //?} else {
        /*return tag.getByteOr("pCol", (byte) 0);
        *///?}
    }

    static byte readPaint(ItemStack stack) {
        //? if <1.20.5 {
        /*CompoundTag tag = stack.getTag();
        return tag != null ? paint(tag) : 0;
        *///?} else {
        return paint(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
        //?}
    }

    static void writePaint(ItemStack stack, byte paint) {
        if (paint <= 0) {
            return;
        }
        //? if <1.20.5 {
        /*stack.getOrCreateTag().putByte("pCol", paint);
        *///?} else {
        CompoundTag tag = new CompoundTag();
        tag.putByte("pCol", paint);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        //?}
        //? if >=1.21.11 {
        /*stack.set(DataComponents.DYED_COLOR, new net.minecraft.world.item.component.DyedItemColor(com.rptubes.tube.PaintColors.rgb(paint)));
        *///?}
    }
}
//?}
