package com.rptubes.worldgen;

import com.rptubes.RPTubes;
import com.rptubes.registry.ModBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 1.16 has no JSON biome modifiers, so tin ore generation (10 veins/chunk,
 * vein size 8, y 0-48 — same numbers as the 1.19.2 datapack) is wired up here.
 */
@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class OreGen {
    private static final ResourceLocation TIN_ORE_ID = new ResourceLocation(RPTubes.MOD_ID, "tin_ore");
    private static ConfiguredFeature<?, ?> tinOre;

    private OreGen() {
    }

    /** Call from FMLCommonSetupEvent.enqueueWork — vanilla registries are not thread-safe. */
    public static void registerFeatures() {
        tinOre = Feature.ORE
                .configured(new OreFeatureConfig(
                        OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                        ModBlocks.TIN_ORE.get().defaultBlockState(), 8))
                .range(48).squared().count(10);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, TIN_ORE_ID, tinOre);
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if (tinOre == null) {
            return;
        }
        Biome.Category category = event.getCategory();
        if (category == Biome.Category.NETHER || category == Biome.Category.THEEND
                || category == Biome.Category.NONE) {
            return;
        }
        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, tinOre);
    }
}
