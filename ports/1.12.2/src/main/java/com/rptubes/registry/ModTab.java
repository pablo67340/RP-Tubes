package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * Lives in its own class so ModBlocks and ModItems can both reference it
 * without triggering each other's static initializers (clinit cycle = NPE).
 */
public final class ModTab {
    public static final CreativeTabs TAB = new CreativeTabs(RPTubes.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.PNEUMATIC_TUBE.get());
        }
    };

    private ModTab() {
    }
}
