package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.blockentity.BufferBlockEntity;
import com.rptubes.blockentity.DeployerBlockEntity;
import com.rptubes.blockentity.EjectorBlockEntity;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.blockentity.RelayBlockEntity;
import com.rptubes.blockentity.RestrictionTubeBlockEntity;
import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.blockentity.TransposerBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public final class ModBlockEntities {
    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE =
            register("alloy_furnace", new BlockEntityType<>(AlloyFurnaceBlockEntity::new, Set.of(ModBlocks.ALLOY_FURNACE)));
    public static final BlockEntityType<TransposerBlockEntity> TRANSPOSER =
            register("transposer", new BlockEntityType<>(TransposerBlockEntity::new, Set.of(ModBlocks.TRANSPOSER)));
    public static final BlockEntityType<FilterBlockEntity> FILTER =
            register("filter", new BlockEntityType<>(FilterBlockEntity::new, Set.of(ModBlocks.FILTER)));
    public static final BlockEntityType<EjectorBlockEntity> EJECTOR =
            register("ejector", new BlockEntityType<>(EjectorBlockEntity::new, Set.of(ModBlocks.EJECTOR)));
    public static final BlockEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER =
            register("block_breaker", new BlockEntityType<>(BlockBreakerBlockEntity::new, Set.of(ModBlocks.BLOCK_BREAKER)));
    public static final BlockEntityType<DeployerBlockEntity> DEPLOYER =
            register("deployer", new BlockEntityType<>(DeployerBlockEntity::new, Set.of(ModBlocks.DEPLOYER)));
    public static final BlockEntityType<BufferBlockEntity> BUFFER =
            register("buffer", new BlockEntityType<>(BufferBlockEntity::new, Set.of(ModBlocks.BUFFER)));
    public static final BlockEntityType<ItemDetectorBlockEntity> ITEM_DETECTOR =
            register("item_detector", new BlockEntityType<>(ItemDetectorBlockEntity::new, Set.of(ModBlocks.ITEM_DETECTOR)));
    public static final BlockEntityType<RelayBlockEntity> RELAY =
            register("relay", new BlockEntityType<>(RelayBlockEntity::new, Set.of(ModBlocks.RELAY)));
    public static final BlockEntityType<RetrieverBlockEntity> RETRIEVER =
            register("retriever", new BlockEntityType<>(RetrieverBlockEntity::new, Set.of(ModBlocks.RETRIEVER)));
    public static final BlockEntityType<RegulatorBlockEntity> REGULATOR =
            register("regulator", new BlockEntityType<>(RegulatorBlockEntity::new, Set.of(ModBlocks.REGULATOR)));
    public static final BlockEntityType<SortingMachineBlockEntity> SORTING_MACHINE =
            register("sorting_machine", new BlockEntityType<>(SortingMachineBlockEntity::new, Set.of(ModBlocks.SORTING_MACHINE)));
    public static final BlockEntityType<IgniterBlockEntity> IGNITER =
            register("igniter", new BlockEntityType<>(IgniterBlockEntity::new, Set.of(ModBlocks.IGNITER)));
    public static final BlockEntityType<AutoCraftingTableBlockEntity> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", new BlockEntityType<>(AutoCraftingTableBlockEntity::new, Set.of(ModBlocks.AUTO_CRAFTING_TABLE)));
    public static final BlockEntityType<AutoCraftingTableMk2BlockEntity> ACT_MK2 =
            register("act_mk2", new BlockEntityType<>(AutoCraftingTableMk2BlockEntity::new, Set.of(ModBlocks.ACT_MK2)));
    public static final BlockEntityType<PneumaticTubeBlockEntity> PNEUMATIC_TUBE =
            register("pneumatic_tube", new BlockEntityType<>(PneumaticTubeBlockEntity::new, Set.of(ModBlocks.PNEUMATIC_TUBE)));
    public static final BlockEntityType<RedstoneTubeBlockEntity> REDSTONE_TUBE =
            register("redstone_tube", new BlockEntityType<>(RedstoneTubeBlockEntity::new, Set.of(ModBlocks.REDSTONE_TUBE)));
    public static final BlockEntityType<RestrictionTubeBlockEntity> RESTRICTION_TUBE =
            register("restriction_tube", new BlockEntityType<>(RestrictionTubeBlockEntity::new, Set.of(ModBlocks.RESTRICTION_TUBE)));

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }

    private ModBlockEntities() {
    }
}
