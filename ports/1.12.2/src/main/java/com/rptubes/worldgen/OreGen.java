package com.rptubes.worldgen;

import com.rptubes.registry.ModBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Ore generation, both toggleable in config for packs that provide their own:
 * tin — 10 veins/chunk, vein size 8, y 0-48 (same numbers as the 1.19.2 datapack);
 * copper — 10 veins/chunk, vein size 8, y 0-64 (vanilla 1.12 has no copper, and
 * brass — the gateway alloy for every tube recipe — needs it).
 */
public class OreGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0) {
            return;
        }
        if (com.rptubes.RPTubesConfig.generateTinOre) {
            WorldGenMinable tin = new WorldGenMinable(
                    ModBlocks.TIN_ORE.get().getDefaultState(), 8);
            for (int i = 0; i < 10; i++) {
                int x = chunkX * 16 + random.nextInt(16);
                int y = random.nextInt(48);
                int z = chunkZ * 16 + random.nextInt(16);
                tin.generate(world, random, new BlockPos(x, y, z));
            }
        }
        if (com.rptubes.RPTubesConfig.generateCopperOre) {
            WorldGenMinable copper = new WorldGenMinable(
                    ModBlocks.COPPER_ORE.get().getDefaultState(), 8);
            for (int i = 0; i < 10; i++) {
                int x = chunkX * 16 + random.nextInt(16);
                int y = random.nextInt(64);
                int z = chunkZ * 16 + random.nextInt(16);
                copper.generate(world, random, new BlockPos(x, y, z));
            }
        }
    }
}
