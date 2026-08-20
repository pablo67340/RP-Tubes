package com.rptubes.worldgen;

import com.rptubes.RPTubes;
import com.rptubes.registry.ModBlocks;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

/**
 * 1.16 has no JSON biome modifiers, so tin ore generation (10 veins/chunk,
 * vein size 8, y 0-48 — same numbers as the 1.19.2 datapack) is wired up here.
 */
public final class OreGen {
    private static final ResourceLocation TIN_ORE_ID = new ResourceLocation(RPTubes.MOD_ID, "tin_ore");

    private OreGen() {
    }

    public static void register() {
        ConfiguredFeature<?, ?> tinOre = Feature.ORE
                .configured(new OreConfiguration(
                        OreConfiguration.Predicates.NATURAL_STONE,
                        ModBlocks.TIN_ORE.defaultBlockState(), 8))
                .range(48).squared().count(10);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, TIN_ORE_ID, tinOre);
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, TIN_ORE_ID));
    }
}
