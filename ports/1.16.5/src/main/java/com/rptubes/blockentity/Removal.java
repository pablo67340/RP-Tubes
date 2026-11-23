package com.rptubes.blockentity;

import net.minecraft.tileentity.TileEntity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * setRemoved runs both when a block is broken and when its chunk unloads. On unload the contents
 * have already been saved with the chunk, so dropping them there duplicates them. Only block
 * entities marked here, by the block's onRemove, may drop their contents.
 */
public final class Removal {
    private static final Set<TileEntity> BROKEN = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private Removal() {
    }

    public static void markBroken(TileEntity be) {
        BROKEN.add(be);
    }

    public static boolean isBroken(TileEntity be) {
        return BROKEN.contains(be);
    }
}
