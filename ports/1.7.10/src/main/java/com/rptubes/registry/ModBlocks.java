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
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public final class ModBlocks {

    private static Block named(Block block, String name) {
        block.setBlockName(RPTubes.MOD_ID + "." + name);
        block.setCreativeTab(ModTab.TAB);
        return block;
    }

    private static Block machine(Block block, String name) {
        named(block, name);
        block.setHardness(2.0F);
        return block;
    }

    private static Block tube(Block block, String name) {
        named(block, name);
        block.setHardness(0.5F);
        return block;
    }

    // OG BlockCustomOre: hardness 3.0, resistance 5.0
    public static final Ref<Block> TIN_ORE = new Ref<Block>(
            named(new Block(Material.rock) { }, "tin_ore")
                    .setHardness(3.0F).setResistance(5.0F).setBlockTextureName("rptubes:tin_ore"));

    public static final Ref<Block> ALLOY_FURNACE = new Ref<Block>(
            named(new AlloyFurnaceBlock(), "alloy_furnace").setHardness(3.5F));

    public static final Ref<Block> TRANSPOSER = new Ref<Block>(machine(new TransposerBlock(), "transposer"));
    public static final Ref<Block> FILTER = new Ref<Block>(machine(new FilterBlock(), "filter"));
    public static final Ref<Block> EJECTOR = new Ref<Block>(machine(new EjectorBlock(), "ejector"));
    public static final Ref<Block> BLOCK_BREAKER = new Ref<Block>(machine(new BlockBreakerBlock(), "block_breaker"));
    public static final Ref<Block> DEPLOYER = new Ref<Block>(machine(new DeployerBlock(), "deployer"));
    public static final Ref<Block> BUFFER = new Ref<Block>(machine(new BufferBlock(), "buffer"));
    public static final Ref<Block> ITEM_DETECTOR = new Ref<Block>(machine(new ItemDetectorBlock(), "item_detector"));
    public static final Ref<Block> RELAY = new Ref<Block>(machine(new RelayBlock(), "relay"));
    public static final Ref<Block> RETRIEVER = new Ref<Block>(machine(new RetrieverBlock(), "retriever"));
    public static final Ref<Block> REGULATOR = new Ref<Block>(machine(new RegulatorBlock(), "regulator"));
    public static final Ref<Block> SORTING_MACHINE = new Ref<Block>(machine(new SortingMachineBlock(), "sorting_machine"));
    public static final Ref<Block> IGNITER = new Ref<Block>(machine(new IgniterBlock(), "igniter"));

    // OG BlockAutoWorkbench: wood material, hardness 1.0, no tool requirement
    public static final Ref<Block> AUTO_CRAFTING_TABLE = new Ref<Block>(
            named(new AutoCraftingTableBlock(), "auto_crafting_table").setHardness(1.0F));

    // OG BlockTubestuff: iron material, hardness 2.0
    public static final Ref<Block> ACT_MK2 = new Ref<Block>(
            named(new AutoCraftingTableMk2Block(), "act_mk2").setHardness(2.0F));

    public static final Ref<Block> PNEUMATIC_TUBE = new Ref<Block>(tube(new PneumaticTubeBlock(), "pneumatic_tube"));
    public static final Ref<Block> REDSTONE_TUBE = new Ref<Block>(tube(new RedstoneTubeBlock(), "redstone_tube"));
    public static final Ref<Block> RESTRICTION_TUBE = new Ref<Block>(tube(new RestrictionTubeBlock(), "restriction_tube"));

    public static void register() {
        GameRegistry.registerBlock(TIN_ORE.get(), "tin_ore");
        GameRegistry.registerBlock(ALLOY_FURNACE.get(), "alloy_furnace");
        GameRegistry.registerBlock(TRANSPOSER.get(), "transposer");
        GameRegistry.registerBlock(FILTER.get(), "filter");
        GameRegistry.registerBlock(EJECTOR.get(), "ejector");
        GameRegistry.registerBlock(BLOCK_BREAKER.get(), "block_breaker");
        GameRegistry.registerBlock(DEPLOYER.get(), "deployer");
        GameRegistry.registerBlock(BUFFER.get(), "buffer");
        GameRegistry.registerBlock(ITEM_DETECTOR.get(), "item_detector");
        GameRegistry.registerBlock(RELAY.get(), "relay");
        GameRegistry.registerBlock(RETRIEVER.get(), "retriever");
        GameRegistry.registerBlock(REGULATOR.get(), "regulator");
        GameRegistry.registerBlock(SORTING_MACHINE.get(), "sorting_machine");
        GameRegistry.registerBlock(IGNITER.get(), "igniter");
        GameRegistry.registerBlock(AUTO_CRAFTING_TABLE.get(), "auto_crafting_table");
        GameRegistry.registerBlock(ACT_MK2.get(), "act_mk2");
        GameRegistry.registerBlock(PNEUMATIC_TUBE.get(), "pneumatic_tube");
        GameRegistry.registerBlock(REDSTONE_TUBE.get(), "redstone_tube");
        GameRegistry.registerBlock(RESTRICTION_TUBE.get(), "restriction_tube");
    }

    private ModBlocks() {
    }
}
