package com.rptubes.registry;

import com.rptubes.RPTubes;
//? if neoforge && <1.21.11 {
import net.minecraft.core.registries.Registries;
//?}
import com.rptubes.block.AlloyFurnaceBlock;
import com.rptubes.block.AutoCraftingTableBlock;
import com.rptubes.block.AutoCraftingTableMk2Block;
import com.rptubes.block.BlockBreakerBlock;
import com.rptubes.block.BufferBlock;
import com.rptubes.block.DeployerBlock;
import com.rptubes.block.EjectorBlock;
import com.rptubes.block.FilterBlock;
import com.rptubes.block.IgniterBlock;
import com.rptubes.block.SolarPanelBlock;
import com.rptubes.block.ThermopileBlock;
import com.rptubes.block.BatteryBoxBlock;
import com.rptubes.block.ItemDetectorBlock;
import com.rptubes.block.PneumaticTubeBlock;
import com.rptubes.block.RedstoneTubeBlock;
import com.rptubes.block.RegulatorBlock;
import com.rptubes.block.RetrievulatorBlock;
import com.rptubes.block.RelayBlock;
import com.rptubes.block.RestrictionTubeBlock;
import com.rptubes.block.MagTubeBlock;
import com.rptubes.block.AcceleratorBlock;
import com.rptubes.block.RetrieverBlock;
import com.rptubes.block.SortingMachineBlock;
import com.rptubes.block.TransposerBlock;
//? if fabric && <1.21.11 {
/*import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
*///?} else if fabric {
/*import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
*///?} else if >=1.21.11 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
*///?}
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if <1.20.1 {
/*import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?} else if fabric {
/*import net.minecraft.world.level.material.MapColor;
*///?} else if forge {
/*import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?} else if <1.21.11 {
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} else {
/*import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

public final class ModBlocks {
    //? if <1.20.1 {
    /*public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RPTubes.MOD_ID);

    private static final BlockBehaviour.Properties MACHINE = BlockBehaviour.Properties.of(Material.STONE)
            .strength(2.0F).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties TUBE = BlockBehaviour.Properties.of(Material.METAL)
            .strength(0.5F).sound(SoundType.STONE).noOcclusion();

    // OG BlockCustomOre: hardness 3.0, resistance 5.0
    public static final RegistryObject<Block> TIN_ORE = BLOCKS.register("tin_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
    *///?} else if fabric && >=1.21.11 && <26.1.2 {
    /*// 1.21.2+: every Block requires its registry id on the Properties before construction,
    // so shared Properties constants are no longer possible.
    private static BlockBehaviour.Properties props(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
    }

    private static BlockBehaviour.Properties machine(String name) {
        return props(name).mapColor(MapColor.STONE).strength(2.0F).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties tube(String name) {
        return props(name).mapColor(MapColor.METAL).strength(0.5F).sound(SoundType.STONE).noOcclusion();
    }

    // OG BlockCustomOre: hardness 3.0, resistance 5.0
    public static final Block TIN_ORE = register("tin_ore",
            new Block(props("tin_ore").mapColor(MapColor.STONE)
    *///?} else if neoforge && <1.21.11 {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, RPTubes.MOD_ID);

    private static final BlockBehaviour.Properties MACHINE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
            .strength(2.0F).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties TUBE = BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
            .strength(0.5F).sound(SoundType.STONE).noOcclusion();

    // OG BlockCustomOre: hardness 3.0, resistance 5.0
    public static final DeferredHolder<Block, Block> TIN_ORE = BLOCKS.register("tin_ore",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
    //?} else if fabric && >=1.21.11 {
    /*// 1.21.2+: every Block requires its registry id on the Properties before construction,
    // so shared Properties constants are no longer possible.
    private static BlockBehaviour.Properties props(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
    }

    private static BlockBehaviour.Properties machine(String name) {
        return props(name).mapColor(MapColor.STONE).strength(2.0F).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties tube(String name) {
        return props(name).mapColor(MapColor.METAL).strength(0.5F).sound(SoundType.STONE).noOcclusion();
    }

    public static final Block TIN_ORE = register("tin_ore",
            new Block(props("tin_ore").mapColor(MapColor.STONE)
    *///?} else if fabric {
    /*private static final BlockBehaviour.Properties MACHINE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
            .strength(2.0F).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties TUBE = BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
            .strength(0.5F).sound(SoundType.STONE).noOcclusion();

    public static final Block TIN_ORE = register("tin_ore",
            new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
    *///?} else if <1.21.11 {
    /*public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RPTubes.MOD_ID);

    private static final BlockBehaviour.Properties MACHINE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
            .strength(2.0F).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties TUBE = BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
            .strength(0.5F).sound(SoundType.STONE).noOcclusion();

    // OG BlockCustomOre: hardness 3.0, resistance 5.0
    public static final RegistryObject<Block> TIN_ORE = BLOCKS.register("tin_ore",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
    *///?} else if forge {
    /*public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RPTubes.MOD_ID);

    // 1.21.2+: every Block requires its registry id on the Properties before construction,
    // so shared Properties constants are no longer possible.
    private static BlockBehaviour.Properties props(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
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
    *///?} else {
    /*public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, RPTubes.MOD_ID);

    // 1.21.2+: every Block requires its registry id on the Properties before construction,
    // so shared Properties constants are no longer possible.
    private static BlockBehaviour.Properties props(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name)));
    }

    private static BlockBehaviour.Properties machine(String name) {
        return props(name).mapColor(MapColor.STONE).strength(2.0F).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties tube(String name) {
        return props(name).mapColor(MapColor.METAL).strength(0.5F).sound(SoundType.STONE).noOcclusion();
    }

    // OG BlockCustomOre: hardness 3.0, resistance 5.0
    public static final DeferredHolder<Block, Block> TIN_ORE = BLOCKS.register("tin_ore",
            () -> new Block(props("tin_ore").mapColor(MapColor.STONE)
    *///?}
                    .strength(3.0F, 5.0F).requiresCorrectToolForDrops()));

    //? if <1.20.1 {
    /*public static final RegistryObject<Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace",
            () -> new AlloyFurnaceBlock(BlockBehaviour.Properties.of(Material.STONE)
    *///?} else if neoforge && <1.21.11 {
    public static final DeferredHolder<Block, Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace",
            () -> new AlloyFurnaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
    //?} else if fabric && <1.21.11 {
    /*public static final Block ALLOY_FURNACE = register("alloy_furnace",
            new AlloyFurnaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
    *///?} else if <1.21.11 {
    /*public static final RegistryObject<Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace",
            () -> new AlloyFurnaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
    *///?} else if fabric {
    /*public static final Block ALLOY_FURNACE = register("alloy_furnace",
            new AlloyFurnaceBlock(props("alloy_furnace").mapColor(MapColor.STONE)
    *///?} else if forge {
    /*public static final RegistryObject<Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace",
            () -> new AlloyFurnaceBlock(props("alloy_furnace").mapColor(MapColor.STONE)
    *///?} else {
    /*public static final DeferredHolder<Block, Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace",
            () -> new AlloyFurnaceBlock(props("alloy_furnace").mapColor(MapColor.STONE)
    *///?}
                    .strength(3.5F).requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AlloyFurnaceBlock.LIT) ? 13 : 0)));

    //? if <1.20.1 {
    /*public static final RegistryObject<Block> TRANSPOSER = BLOCKS.register("transposer",
            () -> new TransposerBlock(MACHINE));
    public static final RegistryObject<Block> FILTER = BLOCKS.register("filter",
            () -> new FilterBlock(MACHINE));
    public static final RegistryObject<Block> EJECTOR = BLOCKS.register("ejector",
            () -> new EjectorBlock(MACHINE));
    public static final RegistryObject<Block> BLOCK_BREAKER = BLOCKS.register("block_breaker",
            () -> new BlockBreakerBlock(MACHINE));
    public static final RegistryObject<Block> DEPLOYER = BLOCKS.register("deployer",
            () -> new DeployerBlock(MACHINE));
    public static final RegistryObject<Block> BUFFER = BLOCKS.register("buffer",
            () -> new BufferBlock(MACHINE));
    public static final RegistryObject<Block> ITEM_DETECTOR = BLOCKS.register("item_detector",
            () -> new ItemDetectorBlock(MACHINE));
    public static final RegistryObject<Block> RELAY = BLOCKS.register("relay",
            () -> new RelayBlock(MACHINE));
    public static final RegistryObject<Block> RETRIEVER = BLOCKS.register("retriever",
            () -> new RetrieverBlock(MACHINE));
    public static final RegistryObject<Block> REGULATOR = BLOCKS.register("regulator",
            () -> new RegulatorBlock(MACHINE));
    public static final RegistryObject<Block> RETRIEVULATOR = BLOCKS.register("retrievulator",
            () -> new RetrievulatorBlock(MACHINE));
    public static final RegistryObject<Block> SORTING_MACHINE = BLOCKS.register("sorting_machine",
            () -> new SortingMachineBlock(MACHINE));
    public static final RegistryObject<Block> IGNITER = BLOCKS.register("igniter",
            () -> new IgniterBlock(MACHINE));
    public static final RegistryObject<Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(MACHINE));
    public static final RegistryObject<Block> THERMOPILE = BLOCKS.register("thermopile",
            () -> new ThermopileBlock(MACHINE));
    public static final RegistryObject<Block> BATTERY_BOX = BLOCKS.register("battery_box",
            () -> new BatteryBoxBlock(MACHINE));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final RegistryObject<Block> AUTO_CRAFTING_TABLE = BLOCKS.register("auto_crafting_table",
            () -> new AutoCraftingTableBlock(BlockBehaviour.Properties.of(Material.WOOD)
    *///?} else if fabric && >=1.21.11 && <26.1.2 {
    /*public static final Block TRANSPOSER = register("transposer", new TransposerBlock(machine("transposer")));
    public static final Block FILTER = register("filter", new FilterBlock(machine("filter")));
    public static final Block EJECTOR = register("ejector", new EjectorBlock(machine("ejector")));
    public static final Block BLOCK_BREAKER = register("block_breaker", new BlockBreakerBlock(machine("block_breaker")));
    public static final Block DEPLOYER = register("deployer", new DeployerBlock(machine("deployer")));
    public static final Block BUFFER = register("buffer", new BufferBlock(machine("buffer")));
    public static final Block ITEM_DETECTOR = register("item_detector", new ItemDetectorBlock(machine("item_detector")));
    public static final Block RELAY = register("relay", new RelayBlock(machine("relay")));
    public static final Block RETRIEVER = register("retriever", new RetrieverBlock(machine("retriever")));
    public static final Block REGULATOR = register("regulator", new RegulatorBlock(machine("regulator")));
    public static final Block RETRIEVULATOR = register("retrievulator", new RetrievulatorBlock(machine("retrievulator")));
    public static final Block SORTING_MACHINE = register("sorting_machine", new SortingMachineBlock(machine("sorting_machine")));
    public static final Block IGNITER = register("igniter", new IgniterBlock(machine("igniter")));
    public static final Block SOLAR_PANEL = register("solar_panel", new SolarPanelBlock(machine("solar_panel")));
    public static final Block THERMOPILE = register("thermopile", new ThermopileBlock(machine("thermopile")));
    public static final Block BATTERY_BOX = register("battery_box", new BatteryBoxBlock(machine("battery_box")));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final Block AUTO_CRAFTING_TABLE = register("auto_crafting_table",
            new AutoCraftingTableBlock(props("auto_crafting_table").mapColor(MapColor.WOOD).ignitedByLava()
    *///?} else if neoforge && <1.21.11 {
    public static final DeferredHolder<Block, Block> TRANSPOSER = BLOCKS.register("transposer",
            () -> new TransposerBlock(MACHINE));
    public static final DeferredHolder<Block, Block> FILTER = BLOCKS.register("filter",
            () -> new FilterBlock(MACHINE));
    public static final DeferredHolder<Block, Block> EJECTOR = BLOCKS.register("ejector",
            () -> new EjectorBlock(MACHINE));
    public static final DeferredHolder<Block, Block> BLOCK_BREAKER = BLOCKS.register("block_breaker",
            () -> new BlockBreakerBlock(MACHINE));
    public static final DeferredHolder<Block, Block> DEPLOYER = BLOCKS.register("deployer",
            () -> new DeployerBlock(MACHINE));
    public static final DeferredHolder<Block, Block> BUFFER = BLOCKS.register("buffer",
            () -> new BufferBlock(MACHINE));
    public static final DeferredHolder<Block, Block> ITEM_DETECTOR = BLOCKS.register("item_detector",
            () -> new ItemDetectorBlock(MACHINE));
    public static final DeferredHolder<Block, Block> RELAY = BLOCKS.register("relay",
            () -> new RelayBlock(MACHINE));
    public static final DeferredHolder<Block, Block> RETRIEVER = BLOCKS.register("retriever",
            () -> new RetrieverBlock(MACHINE));
    public static final DeferredHolder<Block, Block> REGULATOR = BLOCKS.register("regulator",
            () -> new RegulatorBlock(MACHINE));
    public static final DeferredHolder<Block, Block> RETRIEVULATOR = BLOCKS.register("retrievulator",
            () -> new RetrievulatorBlock(MACHINE));
    public static final DeferredHolder<Block, Block> SORTING_MACHINE = BLOCKS.register("sorting_machine",
            () -> new SortingMachineBlock(MACHINE));
    public static final DeferredHolder<Block, Block> IGNITER = BLOCKS.register("igniter",
            () -> new IgniterBlock(MACHINE));
    public static final DeferredHolder<Block, Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(MACHINE));
    public static final DeferredHolder<Block, Block> THERMOPILE = BLOCKS.register("thermopile",
            () -> new ThermopileBlock(MACHINE));
    public static final DeferredHolder<Block, Block> BATTERY_BOX = BLOCKS.register("battery_box",
            () -> new BatteryBoxBlock(MACHINE));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final DeferredHolder<Block, Block> AUTO_CRAFTING_TABLE = BLOCKS.register("auto_crafting_table",
            () -> new AutoCraftingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava()
    //?} else if fabric && >=1.21.11 {
    /*public static final Block TRANSPOSER = register("transposer", new TransposerBlock(machine("transposer")));
    public static final Block FILTER = register("filter", new FilterBlock(machine("filter")));
    public static final Block EJECTOR = register("ejector", new EjectorBlock(machine("ejector")));
    public static final Block BLOCK_BREAKER = register("block_breaker", new BlockBreakerBlock(machine("block_breaker")));
    public static final Block DEPLOYER = register("deployer", new DeployerBlock(machine("deployer")));
    public static final Block BUFFER = register("buffer", new BufferBlock(machine("buffer")));
    public static final Block ITEM_DETECTOR = register("item_detector", new ItemDetectorBlock(machine("item_detector")));
    public static final Block RELAY = register("relay", new RelayBlock(machine("relay")));
    public static final Block RETRIEVER = register("retriever", new RetrieverBlock(machine("retriever")));
    public static final Block REGULATOR = register("regulator", new RegulatorBlock(machine("regulator")));
    public static final Block RETRIEVULATOR = register("retrievulator", new RetrievulatorBlock(machine("retrievulator")));
    public static final Block SORTING_MACHINE = register("sorting_machine", new SortingMachineBlock(machine("sorting_machine")));
    public static final Block IGNITER = register("igniter", new IgniterBlock(machine("igniter")));
    public static final Block SOLAR_PANEL = register("solar_panel", new SolarPanelBlock(machine("solar_panel")));
    public static final Block THERMOPILE = register("thermopile", new ThermopileBlock(machine("thermopile")));
    public static final Block BATTERY_BOX = register("battery_box", new BatteryBoxBlock(machine("battery_box")));

    public static final Block AUTO_CRAFTING_TABLE = register("auto_crafting_table",
            new AutoCraftingTableBlock(props("auto_crafting_table").mapColor(MapColor.WOOD).ignitedByLava()
    *///?} else if fabric {
    /*public static final Block TRANSPOSER = register("transposer", new TransposerBlock(MACHINE));
    public static final Block FILTER = register("filter", new FilterBlock(MACHINE));
    public static final Block EJECTOR = register("ejector", new EjectorBlock(MACHINE));
    public static final Block BLOCK_BREAKER = register("block_breaker", new BlockBreakerBlock(MACHINE));
    public static final Block DEPLOYER = register("deployer", new DeployerBlock(MACHINE));
    public static final Block BUFFER = register("buffer", new BufferBlock(MACHINE));
    public static final Block ITEM_DETECTOR = register("item_detector", new ItemDetectorBlock(MACHINE));
    public static final Block RELAY = register("relay", new RelayBlock(MACHINE));
    public static final Block RETRIEVER = register("retriever", new RetrieverBlock(MACHINE));
    public static final Block REGULATOR = register("regulator", new RegulatorBlock(MACHINE));
    public static final Block RETRIEVULATOR = register("retrievulator", new RetrievulatorBlock(MACHINE));
    public static final Block SORTING_MACHINE = register("sorting_machine", new SortingMachineBlock(MACHINE));
    public static final Block IGNITER = register("igniter", new IgniterBlock(MACHINE));
    public static final Block SOLAR_PANEL = register("solar_panel", new SolarPanelBlock(MACHINE));
    public static final Block THERMOPILE = register("thermopile", new ThermopileBlock(MACHINE));
    public static final Block BATTERY_BOX = register("battery_box", new BatteryBoxBlock(MACHINE));

    public static final Block AUTO_CRAFTING_TABLE = register("auto_crafting_table",
            new AutoCraftingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava()
    *///?} else if <1.21.11 {
    /*public static final RegistryObject<Block> TRANSPOSER = BLOCKS.register("transposer",
            () -> new TransposerBlock(MACHINE));
    public static final RegistryObject<Block> FILTER = BLOCKS.register("filter",
            () -> new FilterBlock(MACHINE));
    public static final RegistryObject<Block> EJECTOR = BLOCKS.register("ejector",
            () -> new EjectorBlock(MACHINE));
    public static final RegistryObject<Block> BLOCK_BREAKER = BLOCKS.register("block_breaker",
            () -> new BlockBreakerBlock(MACHINE));
    public static final RegistryObject<Block> DEPLOYER = BLOCKS.register("deployer",
            () -> new DeployerBlock(MACHINE));
    public static final RegistryObject<Block> BUFFER = BLOCKS.register("buffer",
            () -> new BufferBlock(MACHINE));
    public static final RegistryObject<Block> ITEM_DETECTOR = BLOCKS.register("item_detector",
            () -> new ItemDetectorBlock(MACHINE));
    public static final RegistryObject<Block> RELAY = BLOCKS.register("relay",
            () -> new RelayBlock(MACHINE));
    public static final RegistryObject<Block> RETRIEVER = BLOCKS.register("retriever",
            () -> new RetrieverBlock(MACHINE));
    public static final RegistryObject<Block> REGULATOR = BLOCKS.register("regulator",
            () -> new RegulatorBlock(MACHINE));
    public static final RegistryObject<Block> RETRIEVULATOR = BLOCKS.register("retrievulator",
            () -> new RetrievulatorBlock(MACHINE));
    public static final RegistryObject<Block> SORTING_MACHINE = BLOCKS.register("sorting_machine",
            () -> new SortingMachineBlock(MACHINE));
    public static final RegistryObject<Block> IGNITER = BLOCKS.register("igniter",
            () -> new IgniterBlock(MACHINE));
    public static final RegistryObject<Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(MACHINE));
    public static final RegistryObject<Block> THERMOPILE = BLOCKS.register("thermopile",
            () -> new ThermopileBlock(MACHINE));
    public static final RegistryObject<Block> BATTERY_BOX = BLOCKS.register("battery_box",
            () -> new BatteryBoxBlock(MACHINE));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final RegistryObject<Block> AUTO_CRAFTING_TABLE = BLOCKS.register("auto_crafting_table",
            () -> new AutoCraftingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava()
    *///?} else if forge {
    /*public static final RegistryObject<Block> TRANSPOSER = BLOCKS.register("transposer",
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
    public static final RegistryObject<Block> RETRIEVULATOR = BLOCKS.register("retrievulator",
            () -> new RetrievulatorBlock(machine("retrievulator")));
    public static final RegistryObject<Block> SORTING_MACHINE = BLOCKS.register("sorting_machine",
            () -> new SortingMachineBlock(machine("sorting_machine")));
    public static final RegistryObject<Block> IGNITER = BLOCKS.register("igniter",
            () -> new IgniterBlock(machine("igniter")));
    public static final RegistryObject<Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(machine("solar_panel")));
    public static final RegistryObject<Block> THERMOPILE = BLOCKS.register("thermopile",
            () -> new ThermopileBlock(machine("thermopile")));
    public static final RegistryObject<Block> BATTERY_BOX = BLOCKS.register("battery_box",
            () -> new BatteryBoxBlock(machine("battery_box")));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final RegistryObject<Block> AUTO_CRAFTING_TABLE = BLOCKS.register("auto_crafting_table",
            () -> new AutoCraftingTableBlock(props("auto_crafting_table").mapColor(MapColor.WOOD).ignitedByLava()
    *///?} else {
    /*public static final DeferredHolder<Block, Block> TRANSPOSER = BLOCKS.register("transposer",
            () -> new TransposerBlock(machine("transposer")));
    public static final DeferredHolder<Block, Block> FILTER = BLOCKS.register("filter",
            () -> new FilterBlock(machine("filter")));
    public static final DeferredHolder<Block, Block> EJECTOR = BLOCKS.register("ejector",
            () -> new EjectorBlock(machine("ejector")));
    public static final DeferredHolder<Block, Block> BLOCK_BREAKER = BLOCKS.register("block_breaker",
            () -> new BlockBreakerBlock(machine("block_breaker")));
    public static final DeferredHolder<Block, Block> DEPLOYER = BLOCKS.register("deployer",
            () -> new DeployerBlock(machine("deployer")));
    public static final DeferredHolder<Block, Block> BUFFER = BLOCKS.register("buffer",
            () -> new BufferBlock(machine("buffer")));
    public static final DeferredHolder<Block, Block> ITEM_DETECTOR = BLOCKS.register("item_detector",
            () -> new ItemDetectorBlock(machine("item_detector")));
    public static final DeferredHolder<Block, Block> RELAY = BLOCKS.register("relay",
            () -> new RelayBlock(machine("relay")));
    public static final DeferredHolder<Block, Block> RETRIEVER = BLOCKS.register("retriever",
            () -> new RetrieverBlock(machine("retriever")));
    public static final DeferredHolder<Block, Block> REGULATOR = BLOCKS.register("regulator",
            () -> new RegulatorBlock(machine("regulator")));
    public static final DeferredHolder<Block, Block> RETRIEVULATOR = BLOCKS.register("retrievulator",
            () -> new RetrievulatorBlock(machine("retrievulator")));
    public static final DeferredHolder<Block, Block> SORTING_MACHINE = BLOCKS.register("sorting_machine",
            () -> new SortingMachineBlock(machine("sorting_machine")));
    public static final DeferredHolder<Block, Block> IGNITER = BLOCKS.register("igniter",
            () -> new IgniterBlock(machine("igniter")));
    public static final DeferredHolder<Block, Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(machine("solar_panel")));
    public static final DeferredHolder<Block, Block> THERMOPILE = BLOCKS.register("thermopile",
            () -> new ThermopileBlock(machine("thermopile")));
    public static final DeferredHolder<Block, Block> BATTERY_BOX = BLOCKS.register("battery_box",
            () -> new BatteryBoxBlock(machine("battery_box")));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final DeferredHolder<Block, Block> AUTO_CRAFTING_TABLE = BLOCKS.register("auto_crafting_table",
            () -> new AutoCraftingTableBlock(props("auto_crafting_table").mapColor(MapColor.WOOD).ignitedByLava()
    *///?}
                    .strength(1.0F).sound(SoundType.WOOD)));

    //? if <1.20.1 {
    /*// OG BlockTubestuff: iron material, hardness 2.0
    public static final RegistryObject<Block> ACT_MK2 = BLOCKS.register("act_mk2",
            () -> new AutoCraftingTableMk2Block(BlockBehaviour.Properties.of(Material.METAL)
    *///?} else if fabric && >=1.21.11 && <26.1.2 {
    /*// OG BlockTubestuff: iron material, hardness 2.0
    public static final Block ACT_MK2 = register("act_mk2",
            new AutoCraftingTableMk2Block(props("act_mk2").mapColor(MapColor.METAL)
    *///?} else if neoforge && <1.21.11 {
    // OG BlockTubestuff: iron material, hardness 2.0
    public static final DeferredHolder<Block, Block> ACT_MK2 = BLOCKS.register("act_mk2",
            () -> new AutoCraftingTableMk2Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
    //?} else if fabric && >=1.21.11 {
    /*public static final Block ACT_MK2 = register("act_mk2",
            new AutoCraftingTableMk2Block(props("act_mk2").mapColor(MapColor.METAL)
    *///?} else if fabric {
    /*public static final Block ACT_MK2 = register("act_mk2",
            new AutoCraftingTableMk2Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
    *///?} else if <1.21.11 {
    /*// OG BlockTubestuff: iron material, hardness 2.0
    public static final RegistryObject<Block> ACT_MK2 = BLOCKS.register("act_mk2",
            () -> new AutoCraftingTableMk2Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
    *///?} else if forge {
    /*// OG BlockTubestuff: iron material, hardness 2.0
    public static final RegistryObject<Block> ACT_MK2 = BLOCKS.register("act_mk2",
            () -> new AutoCraftingTableMk2Block(props("act_mk2").mapColor(MapColor.METAL)
    *///?} else {
    /*// OG BlockTubestuff: iron material, hardness 2.0
    public static final DeferredHolder<Block, Block> ACT_MK2 = BLOCKS.register("act_mk2",
            () -> new AutoCraftingTableMk2Block(props("act_mk2").mapColor(MapColor.METAL)
    *///?}
                    .strength(2.0F).sound(SoundType.METAL)));

    //? if fabric && >=1.21.1 && <1.21.11 {
    /*public static final Block PNEUMATIC_TUBE = register("pneumatic_tube", new PneumaticTubeBlock(TUBE));
    public static final Block REDSTONE_TUBE = register("redstone_tube", new RedstoneTubeBlock(TUBE));
    public static final Block RESTRICTION_TUBE = register("restriction_tube", new RestrictionTubeBlock(TUBE));
    public static final Block MAG_TUBE = register("mag_tube", new MagTubeBlock(TUBE));
    public static final Block ACCELERATOR = register("accelerator", new AcceleratorBlock(TUBE));

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), block);
    }

    public static void init() {
    }
    *///?} else if fabric && <1.21.1 {
    /*public static final Block PNEUMATIC_TUBE = register("pneumatic_tube", new PneumaticTubeBlock(TUBE));
    public static final Block REDSTONE_TUBE = register("redstone_tube", new RedstoneTubeBlock(TUBE));
    public static final Block RESTRICTION_TUBE = register("restriction_tube", new RestrictionTubeBlock(TUBE));
    public static final Block MAG_TUBE = register("mag_tube", new MagTubeBlock(TUBE));
    public static final Block ACCELERATOR = register("accelerator", new AcceleratorBlock(TUBE));

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(RPTubes.MOD_ID, name), block);
    }

    public static void init() {
    }
    *///?} else if neoforge && <1.21.11 {
    public static final DeferredHolder<Block, Block> PNEUMATIC_TUBE = BLOCKS.register("pneumatic_tube",
            () -> new PneumaticTubeBlock(TUBE));
    public static final DeferredHolder<Block, Block> REDSTONE_TUBE = BLOCKS.register("redstone_tube",
            () -> new RedstoneTubeBlock(TUBE));
    public static final DeferredHolder<Block, Block> RESTRICTION_TUBE = BLOCKS.register("restriction_tube",
            () -> new RestrictionTubeBlock(TUBE));
    public static final DeferredHolder<Block, Block> MAG_TUBE = BLOCKS.register("mag_tube",
            () -> new MagTubeBlock(TUBE));
    public static final DeferredHolder<Block, Block> ACCELERATOR = BLOCKS.register("accelerator",
            () -> new AcceleratorBlock(TUBE));
    //?} else if fabric {
    /*public static final Block PNEUMATIC_TUBE = register("pneumatic_tube", new PneumaticTubeBlock(tube("pneumatic_tube")));
    public static final Block REDSTONE_TUBE = register("redstone_tube", new RedstoneTubeBlock(tube("redstone_tube")));
    public static final Block RESTRICTION_TUBE = register("restriction_tube", new RestrictionTubeBlock(tube("restriction_tube")));
    public static final Block MAG_TUBE = register("mag_tube", new MagTubeBlock(tube("mag_tube")));
    public static final Block ACCELERATOR = register("accelerator", new AcceleratorBlock(tube("accelerator")));

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), block);
    }

    public static void init() {
    }
    *///?} else if forge && >=1.21.11 {
    /*public static final RegistryObject<Block> PNEUMATIC_TUBE = BLOCKS.register("pneumatic_tube",
            () -> new PneumaticTubeBlock(tube("pneumatic_tube")));
    public static final RegistryObject<Block> REDSTONE_TUBE = BLOCKS.register("redstone_tube",
            () -> new RedstoneTubeBlock(tube("redstone_tube")));
    public static final RegistryObject<Block> RESTRICTION_TUBE = BLOCKS.register("restriction_tube",
            () -> new RestrictionTubeBlock(tube("restriction_tube")));
    public static final RegistryObject<Block> MAG_TUBE = BLOCKS.register("mag_tube",
            () -> new MagTubeBlock(tube("mag_tube")));
    public static final RegistryObject<Block> ACCELERATOR = BLOCKS.register("accelerator",
            () -> new AcceleratorBlock(tube("accelerator")));
    *///?} else if >=1.21.11 {
    /*public static final DeferredHolder<Block, Block> PNEUMATIC_TUBE = BLOCKS.register("pneumatic_tube",
            () -> new PneumaticTubeBlock(tube("pneumatic_tube")));
    public static final DeferredHolder<Block, Block> REDSTONE_TUBE = BLOCKS.register("redstone_tube",
            () -> new RedstoneTubeBlock(tube("redstone_tube")));
    public static final DeferredHolder<Block, Block> RESTRICTION_TUBE = BLOCKS.register("restriction_tube",
            () -> new RestrictionTubeBlock(tube("restriction_tube")));
    public static final DeferredHolder<Block, Block> MAG_TUBE = BLOCKS.register("mag_tube",
            () -> new MagTubeBlock(tube("mag_tube")));
    public static final DeferredHolder<Block, Block> ACCELERATOR = BLOCKS.register("accelerator",
            () -> new AcceleratorBlock(tube("accelerator")));
    *///?} else {
    /*public static final RegistryObject<Block> PNEUMATIC_TUBE = BLOCKS.register("pneumatic_tube",
            () -> new PneumaticTubeBlock(TUBE));
    public static final RegistryObject<Block> REDSTONE_TUBE = BLOCKS.register("redstone_tube",
            () -> new RedstoneTubeBlock(TUBE));
    public static final RegistryObject<Block> RESTRICTION_TUBE = BLOCKS.register("restriction_tube",
            () -> new RestrictionTubeBlock(TUBE));
    public static final RegistryObject<Block> MAG_TUBE = BLOCKS.register("mag_tube",
            () -> new MagTubeBlock(TUBE));
    public static final RegistryObject<Block> ACCELERATOR = BLOCKS.register("accelerator",
            () -> new AcceleratorBlock(TUBE));
    *///?}

    private ModBlocks() {
    }
}
