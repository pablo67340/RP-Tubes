package com.rptubes.client;

import com.mojang.serialization.MapCodec;
import com.rptubes.tube.PaintColors;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.Nullable;

public enum TubePaintTintSource implements ItemTintSource {
    INSTANCE;

    public static final MapCodec<TubePaintTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        int rgb = data == null ? 0xFFFFFF : PaintColors.rgb(data.copyTag().getByteOr("pCol", (byte) 0));
        return ARGB.opaque(rgb);
    }

    @Override
    public MapCodec<TubePaintTintSource> type() {
        return MAP_CODEC;
    }
}
