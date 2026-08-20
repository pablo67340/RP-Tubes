package com.rptubes;

import com.rptubes.compat.ProjectECompat;
import com.rptubes.item.ScrewdriverItem;
import com.rptubes.network.ModNetworking;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.registry.ModRecipes;
import com.rptubes.worldgen.OreGen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RPTubes implements ModInitializer {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        ModNetworking.register();
        OreGen.register();

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.getItemInHand(hand).getItem() instanceof ScrewdriverItem) {
                return ScrewdriverItem.tryRotate(player.getItemInHand(hand), level, hit.getBlockPos(), player, hand);
            }
            return InteractionResult.PASS;
        });

        ProjectECompat.tryRegister();
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
