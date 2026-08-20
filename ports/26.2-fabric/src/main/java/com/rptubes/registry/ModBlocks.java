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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    // 1.21.2+: every Block requires its registry id on the Properties before construction,
    // so shared Properties constants are no longer possible.
    private static BlockBehaviour.Properties props(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
    }

    private static BlockBehaviour.Properties machine(String name) {
        return props(name).mapColor(MapColor.STONE).strength(2.0F).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties tube(String name) {
        return props(name).mapColor(MapColor.METAL).strength(0.5F).sound(SoundType.STONE).noOcclusion();
    }

    public static final Block TIN_ORE = register("tin_ore",
            new Block(props("tin_ore").mapColor(MapColor.STONE)
                    .strength(3.0F, 5.0F).requiresCorrectToolForDrops()));

    public static final Block ALLOY_FURNACE = register("alloy_furnace",
            new AlloyFurnaceBlock(props("alloy_furnace").mapColor(MapColor.STONE)
                    .strength(3.5F).requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AlloyFurnaceBlock.LIT) ? 13 : 0)));

    public static final Block TRANSPOSER = register("transposer", new TransposerBlock(machine("transposer")));
    public static final Block FILTER = register("filter", new FilterBlock(machine("filter")));
    public static final Block EJECTOR = register("ejector", new EjectorBlock(machine("ejector")));
    public static final Block BLOCK_BREAKER = register("block_breaker", new BlockBreakerBlock(machine("block_breaker")));
    public static final Block DEPLOYER = register("deployer", new DeployerBlock(machine("deployer")));
    public static final Block BUFFER = register("buffer", new BufferBlock(machine("buffer")));
    public static final Block ITEM_DETECTOR = register("item_detector", new ItemDetectorBlock(machine("item_detector")));
    public static final Block RELAY = register("relay", new RelayBlock(machine("relay")));
    public static final Block RETRIEVER = register("retriever", new RetrieverBlock(machine("retriever")));
    public static final Block REGULATOR = register("regulator", new RegulatorBlock(machine("regulator")));
    public static final Block SORTING_MACHINE = register("sorting_machine", new SortingMachineBlock(machine("sorting_machine")));
    public static final Block IGNITER = register("igniter", new IgniterBlock(machine("igniter")));

    public static final Block AUTO_CRAFTING_TABLE = register("auto_crafting_table",
            new AutoCraftingTableBlock(props("auto_crafting_table").mapColor(MapColor.WOOD).ignitedByLava()
                    .strength(1.0F).sound(SoundType.WOOD)));

    public static final Block ACT_MK2 = register("act_mk2",
            new AutoCraftingTableMk2Block(props("act_mk2").mapColor(MapColor.METAL)
                    .strength(2.0F).sound(SoundType.METAL)));

    public static final Block PNEUMATIC_TUBE = register("pneumatic_tube", new PneumaticTubeBlock(tube("pneumatic_tube")));
    public static final Block REDSTONE_TUBE = register("redstone_tube", new RedstoneTubeBlock(tube("redstone_tube")));
    public static final Block RESTRICTION_TUBE = register("restriction_tube", new RestrictionTubeBlock(tube("restriction_tube")));

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name), block);
    }

    public static void init() {
    }

    private ModBlocks() {
    }
}
