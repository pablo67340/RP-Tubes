//? if >=1.21.11 {
/*package com.rptubes.client;

import com.mojang.serialization.MapCodec;
//? if fabric {
/^import com.rptubes.tube.DyeHelper;
^///?}
import com.rptubes.tube.PaintColors;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
//? if forgelike {
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
//?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
//? if fabric {

/^/^* Item-model tint for painted tubes (ColorProviderRegistry.ITEM is gone in 1.21.4+). ^/
public record TubePaintTintSource() implements ItemTintSource {
    public static final TubePaintTintSource INSTANCE = new TubePaintTintSource();
^///?} else {
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.Nullable;

public enum TubePaintTintSource implements ItemTintSource {
    INSTANCE;

//?}
    public static final MapCodec<TubePaintTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    //? if fabric {
    /^public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        return PaintColors.rgb(DyeHelper.readPaint(stack));
    ^///?} else {
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        int rgb = data == null ? 0xFFFFFF : PaintColors.rgb(data.copyTag().getByteOr("pCol", (byte) 0));
        return ARGB.opaque(rgb);
    //?}
    }

    @Override
    //? if fabric {
    /^public MapCodec<? extends ItemTintSource> type() {
    ^///?} else {
    public MapCodec<TubePaintTintSource> type() {
    //?}
        return MAP_CODEC;
    }
}
*///?}
