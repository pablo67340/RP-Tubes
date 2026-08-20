package com.rptubes.registry;

import com.rptubes.RPTubes;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RPTubes.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE =
            BLOCK_ENTITIES.register("alloy_furnace",
                    () -> BlockEntityType.Builder.of(AlloyFurnaceBlockEntity::new, ModBlocks.ALLOY_FURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransposerBlockEntity>> TRANSPOSER =
            BLOCK_ENTITIES.register("transposer",
                    () -> BlockEntityType.Builder.of(TransposerBlockEntity::new, ModBlocks.TRANSPOSER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FilterBlockEntity>> FILTER =
            BLOCK_ENTITIES.register("filter",
                    () -> BlockEntityType.Builder.of(FilterBlockEntity::new, ModBlocks.FILTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EjectorBlockEntity>> EJECTOR =
            BLOCK_ENTITIES.register("ejector",
                    () -> BlockEntityType.Builder.of(EjectorBlockEntity::new, ModBlocks.EJECTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockBreakerBlockEntity>> BLOCK_BREAKER =
            BLOCK_ENTITIES.register("block_breaker",
                    () -> BlockEntityType.Builder.of(BlockBreakerBlockEntity::new, ModBlocks.BLOCK_BREAKER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeployerBlockEntity>> DEPLOYER =
            BLOCK_ENTITIES.register("deployer",
                    () -> BlockEntityType.Builder.of(DeployerBlockEntity::new, ModBlocks.DEPLOYER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BufferBlockEntity>> BUFFER =
            BLOCK_ENTITIES.register("buffer",
                    () -> BlockEntityType.Builder.of(BufferBlockEntity::new, ModBlocks.BUFFER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDetectorBlockEntity>> ITEM_DETECTOR =
            BLOCK_ENTITIES.register("item_detector",
                    () -> BlockEntityType.Builder.of(ItemDetectorBlockEntity::new, ModBlocks.ITEM_DETECTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RelayBlockEntity>> RELAY =
            BLOCK_ENTITIES.register("relay",
                    () -> BlockEntityType.Builder.of(RelayBlockEntity::new, ModBlocks.RELAY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RetrieverBlockEntity>> RETRIEVER =
            BLOCK_ENTITIES.register("retriever",
                    () -> BlockEntityType.Builder.of(RetrieverBlockEntity::new, ModBlocks.RETRIEVER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RegulatorBlockEntity>> REGULATOR =
            BLOCK_ENTITIES.register("regulator",
                    () -> BlockEntityType.Builder.of(RegulatorBlockEntity::new, ModBlocks.REGULATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SortingMachineBlockEntity>> SORTING_MACHINE =
            BLOCK_ENTITIES.register("sorting_machine",
                    () -> BlockEntityType.Builder.of(SortingMachineBlockEntity::new, ModBlocks.SORTING_MACHINE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IgniterBlockEntity>> IGNITER =
            BLOCK_ENTITIES.register("igniter",
                    () -> BlockEntityType.Builder.of(IgniterBlockEntity::new, ModBlocks.IGNITER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoCraftingTableBlockEntity>> AUTO_CRAFTING_TABLE =
            BLOCK_ENTITIES.register("auto_crafting_table",
                    () -> BlockEntityType.Builder.of(AutoCraftingTableBlockEntity::new, ModBlocks.AUTO_CRAFTING_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoCraftingTableMk2BlockEntity>> ACT_MK2 =
            BLOCK_ENTITIES.register("act_mk2",
                    () -> BlockEntityType.Builder.of(AutoCraftingTableMk2BlockEntity::new, ModBlocks.ACT_MK2.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PneumaticTubeBlockEntity>> PNEUMATIC_TUBE =
            BLOCK_ENTITIES.register("pneumatic_tube",
                    () -> BlockEntityType.Builder.of(PneumaticTubeBlockEntity::new, ModBlocks.PNEUMATIC_TUBE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneTubeBlockEntity>> REDSTONE_TUBE =
            BLOCK_ENTITIES.register("redstone_tube",
                    () -> BlockEntityType.Builder.of(RedstoneTubeBlockEntity::new, ModBlocks.REDSTONE_TUBE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RestrictionTubeBlockEntity>> RESTRICTION_TUBE =
            BLOCK_ENTITIES.register("restriction_tube",
                    () -> BlockEntityType.Builder.of(RestrictionTubeBlockEntity::new, ModBlocks.RESTRICTION_TUBE.get()).build(null));

    private ModBlockEntities() {
    }
}
