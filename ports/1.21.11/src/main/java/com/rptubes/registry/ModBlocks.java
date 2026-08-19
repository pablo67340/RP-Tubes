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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RPTubes.MOD_ID);

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

    // OG BlockCustomOre: hardness 3.0, resistance 5.0
    public static final RegistryObject<Block> TIN_ORE = BLOCKS.register("tin_ore",
            () -> new Block(props("tin_ore").mapColor(MapColor.STONE)
                    .strength(3.0F, 5.0F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace",
            () -> new AlloyFurnaceBlock(props("alloy_furnace").mapColor(MapColor.STONE)
                    .strength(3.5F).requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AlloyFurnaceBlock.LIT) ? 13 : 0)));

    public static final RegistryObject<Block> TRANSPOSER = BLOCKS.register("transposer",
            () -> new TransposerBlock(machine("transposer")));
    public static final RegistryObject<Block> FILTER = BLOCKS.register("filter",
            () -> new FilterBlock(machine("filter")));
    public static final RegistryObject<Block> EJECTOR = BLOCKS.register("ejector",
            () -> new EjectorBlock(machine("ejector")));
    public static final RegistryObject<Block> BLOCK_BREAKER = BLOCKS.register("block_breaker",
            () -> new BlockBreakerBlock(machine("block_breaker")));
    public static final RegistryObject<Block> DEPLOYER = BLOCKS.register("deployer",
            () -> new DeployerBlock(machine("deployer")));
    public static final RegistryObject<Block> BUFFER = BLOCKS.register("buffer",
            () -> new BufferBlock(machine("buffer")));
    public static final RegistryObject<Block> ITEM_DETECTOR = BLOCKS.register("item_detector",
            () -> new ItemDetectorBlock(machine("item_detector")));
    public static final RegistryObject<Block> RELAY = BLOCKS.register("relay",
            () -> new RelayBlock(machine("relay")));
    public static final RegistryObject<Block> RETRIEVER = BLOCKS.register("retriever",
            () -> new RetrieverBlock(machine("retriever")));
    public static final RegistryObject<Block> REGULATOR = BLOCKS.register("regulator",
            () -> new RegulatorBlock(machine("regulator")));
    public static final RegistryObject<Block> SORTING_MACHINE = BLOCKS.register("sorting_machine",
            () -> new SortingMachineBlock(machine("sorting_machine")));
    public static final RegistryObject<Block> IGNITER = BLOCKS.register("igniter",
            () -> new IgniterBlock(machine("igniter")));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final RegistryObject<Block> AUTO_CRAFTING_TABLE = BLOCKS.register("auto_crafting_table",
            () -> new AutoCraftingTableBlock(props("auto_crafting_table").mapColor(MapColor.WOOD).ignitedByLava()
                    .strength(1.0F).sound(SoundType.WOOD)));

    // OG BlockTubestuff: iron material, hardness 2.0
    public static final RegistryObject<Block> ACT_MK2 = BLOCKS.register("act_mk2",
            () -> new AutoCraftingTableMk2Block(props("act_mk2").mapColor(MapColor.METAL)
                    .strength(2.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> PNEUMATIC_TUBE = BLOCKS.register("pneumatic_tube",
            () -> new PneumaticTubeBlock(tube("pneumatic_tube")));
    public static final RegistryObject<Block> REDSTONE_TUBE = BLOCKS.register("redstone_tube",
            () -> new RedstoneTubeBlock(tube("redstone_tube")));
    public static final RegistryObject<Block> RESTRICTION_TUBE = BLOCKS.register("restriction_tube",
            () -> new RestrictionTubeBlock(tube("restriction_tube")));

    private ModBlocks() {
    }
}
