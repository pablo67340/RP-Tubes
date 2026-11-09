package com.rptubes;

import com.rptubes.compat.ProjectECompat;
import com.rptubes.item.ScrewdriverItem;
import com.rptubes.network.ModNetworking;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.registry.ModRecipes;
import com.rptubes.registry.ModStorage;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.slf4j.Logger;

public class RPTubes implements ModInitializer {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        RPTubesConfig.load();
        ModStorage.register();
        try {
            Class.forName("team.reborn.energy.api.EnergyStorage");
            Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
        } catch (Throwable e) {
            LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
        }
        ModNetworking.register();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MOD_ID, "tin_ore")));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.getItemInHand(hand).getItem() instanceof ScrewdriverItem) {
                return ScrewdriverItem.tryRotate(player.getItemInHand(hand), level, hit.getBlockPos(), player, hand);
            }
            InteractionResult paint = com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
            if (paint.consumesAction()) {
                return paint;
            }
            return InteractionResult.PASS;
        });

        ProjectECompat.tryRegister();
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
