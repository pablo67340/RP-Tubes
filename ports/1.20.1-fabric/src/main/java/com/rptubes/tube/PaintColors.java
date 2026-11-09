package com.rptubes.tube;

import net.minecraft.world.item.DyeColor;

/**
 * RedPower paint palette: 0 = unpainted, 1-16 = vanilla dye id + 1.
 * RGB values match OG RenderTube / GuiSorter swatches.
 */
public final class PaintColors {
    public static final int[] RGB = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C6CFF, 0xFFFF00, 0x00FF00, 0xFF6666, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2100, 0x008000, 0xFF0000, 0x1F1F1F
    };

    private PaintColors() {
    }

    /** Packed 0xRRGGBB for paint 1-16, or white if unpainted. */
    public static int rgb(int paint) {
        if (paint <= 0 || paint > 16) {
            return 0xFFFFFF;
        }
        return RGB[paint - 1];
    }

    public static int fromDye(DyeColor dye) {
        return dye == null ? 0 : dye.getId() + 1;
    }
}
