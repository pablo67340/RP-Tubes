package com.rptubes;

import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.registry.ModRecipes;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RPTubes.MOD_ID)
public class RPTubes {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Version-checked handshake. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     */
    public static final String PROTOCOL = "10200";

    public RPTubes(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
