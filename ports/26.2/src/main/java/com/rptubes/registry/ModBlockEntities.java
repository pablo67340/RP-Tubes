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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RPTubes.MOD_ID);

    public static final RegistryObject<BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE =
            BLOCK_ENTITIES.register("alloy_furnace",
                    () -> new BlockEntityType<>(AlloyFurnaceBlockEntity::new, java.util.Set.of(ModBlocks.ALLOY_FURNACE.get())));

    public static final RegistryObject<BlockEntityType<TransposerBlockEntity>> TRANSPOSER =
            BLOCK_ENTITIES.register("transposer",
                    () -> new BlockEntityType<>(TransposerBlockEntity::new, java.util.Set.of(ModBlocks.TRANSPOSER.get())));

    public static final RegistryObject<BlockEntityType<FilterBlockEntity>> FILTER =
            BLOCK_ENTITIES.register("filter",
                    () -> new BlockEntityType<>(FilterBlockEntity::new, java.util.Set.of(ModBlocks.FILTER.get())));

    public static final RegistryObject<BlockEntityType<EjectorBlockEntity>> EJECTOR =
            BLOCK_ENTITIES.register("ejector",
                    () -> new BlockEntityType<>(EjectorBlockEntity::new, java.util.Set.of(ModBlocks.EJECTOR.get())));

    public static final RegistryObject<BlockEntityType<BlockBreakerBlockEntity>> BLOCK_BREAKER =
            BLOCK_ENTITIES.register("block_breaker",
                    () -> new BlockEntityType<>(BlockBreakerBlockEntity::new, java.util.Set.of(ModBlocks.BLOCK_BREAKER.get())));

    public static final RegistryObject<BlockEntityType<DeployerBlockEntity>> DEPLOYER =
            BLOCK_ENTITIES.register("deployer",
                    () -> new BlockEntityType<>(DeployerBlockEntity::new, java.util.Set.of(ModBlocks.DEPLOYER.get())));

    public static final RegistryObject<BlockEntityType<BufferBlockEntity>> BUFFER =
            BLOCK_ENTITIES.register("buffer",
                    () -> new BlockEntityType<>(BufferBlockEntity::new, java.util.Set.of(ModBlocks.BUFFER.get())));

    public static final RegistryObject<BlockEntityType<ItemDetectorBlockEntity>> ITEM_DETECTOR =
            BLOCK_ENTITIES.register("item_detector",
                    () -> new BlockEntityType<>(ItemDetectorBlockEntity::new, java.util.Set.of(ModBlocks.ITEM_DETECTOR.get())));

    public static final RegistryObject<BlockEntityType<RelayBlockEntity>> RELAY =
            BLOCK_ENTITIES.register("relay",
                    () -> new BlockEntityType<>(RelayBlockEntity::new, java.util.Set.of(ModBlocks.RELAY.get())));

    public static final RegistryObject<BlockEntityType<RetrieverBlockEntity>> RETRIEVER =
            BLOCK_ENTITIES.register("retriever",
                    () -> new BlockEntityType<>(RetrieverBlockEntity::new, java.util.Set.of(ModBlocks.RETRIEVER.get())));

    public static final RegistryObject<BlockEntityType<RegulatorBlockEntity>> REGULATOR =
            BLOCK_ENTITIES.register("regulator",
                    () -> new BlockEntityType<>(RegulatorBlockEntity::new, java.util.Set.of(ModBlocks.REGULATOR.get())));

    public static final RegistryObject<BlockEntityType<SortingMachineBlockEntity>> SORTING_MACHINE =
            BLOCK_ENTITIES.register("sorting_machine",
                    () -> new BlockEntityType<>(SortingMachineBlockEntity::new, java.util.Set.of(ModBlocks.SORTING_MACHINE.get())));

    public static final RegistryObject<BlockEntityType<IgniterBlockEntity>> IGNITER =
            BLOCK_ENTITIES.register("igniter",
                    () -> new BlockEntityType<>(IgniterBlockEntity::new, java.util.Set.of(ModBlocks.IGNITER.get())));

    public static final RegistryObject<BlockEntityType<AutoCraftingTableBlockEntity>> AUTO_CRAFTING_TABLE =
            BLOCK_ENTITIES.register("auto_crafting_table",
                    () -> new BlockEntityType<>(AutoCraftingTableBlockEntity::new, java.util.Set.of(ModBlocks.AUTO_CRAFTING_TABLE.get())));

    public static final RegistryObject<BlockEntityType<AutoCraftingTableMk2BlockEntity>> ACT_MK2 =
            BLOCK_ENTITIES.register("act_mk2",
                    () -> new BlockEntityType<>(AutoCraftingTableMk2BlockEntity::new, java.util.Set.of(ModBlocks.ACT_MK2.get())));

    public static final RegistryObject<BlockEntityType<PneumaticTubeBlockEntity>> PNEUMATIC_TUBE =
            BLOCK_ENTITIES.register("pneumatic_tube",
                    () -> new BlockEntityType<>(PneumaticTubeBlockEntity::new, java.util.Set.of(ModBlocks.PNEUMATIC_TUBE.get())));

    public static final RegistryObject<BlockEntityType<RedstoneTubeBlockEntity>> REDSTONE_TUBE =
            BLOCK_ENTITIES.register("redstone_tube",
                    () -> new BlockEntityType<>(RedstoneTubeBlockEntity::new, java.util.Set.of(ModBlocks.REDSTONE_TUBE.get())));

    public static final RegistryObject<BlockEntityType<RestrictionTubeBlockEntity>> RESTRICTION_TUBE =
            BLOCK_ENTITIES.register("restriction_tube",
                    () -> new BlockEntityType<>(RestrictionTubeBlockEntity::new, java.util.Set.of(ModBlocks.RESTRICTION_TUBE.get())));

    private ModBlockEntities() {
    }
}
