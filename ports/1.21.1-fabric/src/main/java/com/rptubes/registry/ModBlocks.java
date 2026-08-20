package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.block.AlloyFurnaceBlock;
import com.rptubes.block.AutoCraftingTableBlock;
import com.rptubes.block.AutoCraftingTableMk2Block;
import com.rptubes.block.BlockBreakerBlock;
import com.rptubes.block.BufferBlock;
import com.rptubes.block.DeployerBlock;
import com.rptubes.block.EjectorBlock;
import com.rptubes.block.FilterBlock;
import com.rptubes.block.IgniterBlock;
import com.rptubes.block.ItemDetectorBlock;
import com.rptubes.block.PneumaticTubeBlock;
import com.rptubes.block.RedstoneTubeBlock;
import com.rptubes.block.RegulatorBlock;
import com.rptubes.block.RelayBlock;
import com.rptubes.block.RestrictionTubeBlock;
import com.rptubes.block.RetrieverBlock;
import com.rptubes.block.SortingMachineBlock;
import com.rptubes.block.TransposerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    private static final BlockBehaviour.Properties MACHINE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
            .strength(2.0F).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties TUBE = BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
            .strength(0.5F).sound(SoundType.STONE).noOcclusion();

    public static final Block TIN_ORE = register("tin_ore",
            new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(3.0F, 5.0F).requiresCorrectToolForDrops()));

    public static final Block ALLOY_FURNACE = register("alloy_furnace",
            new AlloyFurnaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(3.5F).requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AlloyFurnaceBlock.LIT) ? 13 : 0)));

    public static final Block TRANSPOSER = register("transposer", new TransposerBlock(MACHINE));
    public static final Block FILTER = register("filter", new FilterBlock(MACHINE));
    public static final Block EJECTOR = register("ejector", new EjectorBlock(MACHINE));
    public static final Block BLOCK_BREAKER = register("block_breaker", new BlockBreakerBlock(MACHINE));
    public static final Block DEPLOYER = register("deployer", new DeployerBlock(MACHINE));
    public static final Block BUFFER = register("buffer", new BufferBlock(MACHINE));
    public static final Block ITEM_DETECTOR = register("item_detector", new ItemDetectorBlock(MACHINE));
    public static final Block RELAY = register("relay", new RelayBlock(MACHINE));
    public static final Block RETRIEVER = register("retriever", new RetrieverBlock(MACHINE));
    public static final Block REGULATOR = register("regulator", new RegulatorBlock(MACHINE));
    public static final Block SORTING_MACHINE = register("sorting_machine", new SortingMachineBlock(MACHINE));
    public static final Block IGNITER = register("igniter", new IgniterBlock(MACHINE));

    public static final Block AUTO_CRAFTING_TABLE = register("auto_crafting_table",
            new AutoCraftingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava()
                    .strength(1.0F).sound(SoundType.WOOD)));

    public static final Block ACT_MK2 = register("act_mk2",
            new AutoCraftingTableMk2Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .strength(2.0F).sound(SoundType.METAL)));

    public static final Block PNEUMATIC_TUBE = register("pneumatic_tube", new PneumaticTubeBlock(TUBE));
    public static final Block REDSTONE_TUBE = register("redstone_tube", new RedstoneTubeBlock(TUBE));
    public static final Block RESTRICTION_TUBE = register("restriction_tube", new RestrictionTubeBlock(TUBE));

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), block);
    }

    public static void init() {
    }

    private ModBlocks() {
    }
}
