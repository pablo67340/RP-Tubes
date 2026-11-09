package com.rptubes.client;

import com.mojang.serialization.MapCodec;
import com.rptubes.tube.DyeHelper;
import com.rptubes.tube.PaintColors;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Item-model tint for painted tubes (ColorProviderRegistry.ITEM is gone in 1.21.4+). */
public record TubePaintTintSource() implements ItemTintSource {
    public static final TubePaintTintSource INSTANCE = new TubePaintTintSource();
    public static final MapCodec<TubePaintTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        return PaintColors.rgb(DyeHelper.readPaint(stack));
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
