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
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPTubes.MOD_ID)
public final class ModBlocks {

    private static Block named(Block block, String name) {
        block.setRegistryName(new ResourceLocation(RPTubes.MOD_ID, name));
        block.setTranslationKey(RPTubes.MOD_ID + "." + name);
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
            named(new Block(Material.ROCK), "tin_ore").setHardness(3.0F).setResistance(5.0F));

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

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                TIN_ORE.get(), ALLOY_FURNACE.get(), TRANSPOSER.get(), FILTER.get(), EJECTOR.get(),
                BLOCK_BREAKER.get(), DEPLOYER.get(), BUFFER.get(), ITEM_DETECTOR.get(), RELAY.get(),
                RETRIEVER.get(), REGULATOR.get(), SORTING_MACHINE.get(), IGNITER.get(),
                AUTO_CRAFTING_TABLE.get(), ACT_MK2.get(),
                PNEUMATIC_TUBE.get(), REDSTONE_TUBE.get(), RESTRICTION_TUBE.get());
    }

    private ModBlocks() {
    }
}
