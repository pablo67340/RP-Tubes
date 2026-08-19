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
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, RPTubes.MOD_ID);

    public static final RegistryObject<TileEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE =
            BLOCK_ENTITIES.register("alloy_furnace",
                    () -> TileEntityType.Builder.of(AlloyFurnaceBlockEntity::new, ModBlocks.ALLOY_FURNACE.get()).build(null));

    public static final RegistryObject<TileEntityType<TransposerBlockEntity>> TRANSPOSER =
            BLOCK_ENTITIES.register("transposer",
                    () -> TileEntityType.Builder.of(TransposerBlockEntity::new, ModBlocks.TRANSPOSER.get()).build(null));

    public static final RegistryObject<TileEntityType<FilterBlockEntity>> FILTER =
            BLOCK_ENTITIES.register("filter",
                    () -> TileEntityType.Builder.of(FilterBlockEntity::new, ModBlocks.FILTER.get()).build(null));

    public static final RegistryObject<TileEntityType<EjectorBlockEntity>> EJECTOR =
            BLOCK_ENTITIES.register("ejector",
                    () -> TileEntityType.Builder.of(EjectorBlockEntity::new, ModBlocks.EJECTOR.get()).build(null));

    public static final RegistryObject<TileEntityType<BlockBreakerBlockEntity>> BLOCK_BREAKER =
            BLOCK_ENTITIES.register("block_breaker",
                    () -> TileEntityType.Builder.of(BlockBreakerBlockEntity::new, ModBlocks.BLOCK_BREAKER.get()).build(null));

    public static final RegistryObject<TileEntityType<DeployerBlockEntity>> DEPLOYER =
            BLOCK_ENTITIES.register("deployer",
                    () -> TileEntityType.Builder.of(DeployerBlockEntity::new, ModBlocks.DEPLOYER.get()).build(null));

    public static final RegistryObject<TileEntityType<BufferBlockEntity>> BUFFER =
            BLOCK_ENTITIES.register("buffer",
                    () -> TileEntityType.Builder.of(BufferBlockEntity::new, ModBlocks.BUFFER.get()).build(null));

    public static final RegistryObject<TileEntityType<ItemDetectorBlockEntity>> ITEM_DETECTOR =
            BLOCK_ENTITIES.register("item_detector",
                    () -> TileEntityType.Builder.of(ItemDetectorBlockEntity::new, ModBlocks.ITEM_DETECTOR.get()).build(null));

    public static final RegistryObject<TileEntityType<RelayBlockEntity>> RELAY =
            BLOCK_ENTITIES.register("relay",
                    () -> TileEntityType.Builder.of(RelayBlockEntity::new, ModBlocks.RELAY.get()).build(null));

    public static final RegistryObject<TileEntityType<RetrieverBlockEntity>> RETRIEVER =
            BLOCK_ENTITIES.register("retriever",
                    () -> TileEntityType.Builder.of(RetrieverBlockEntity::new, ModBlocks.RETRIEVER.get()).build(null));

    public static final RegistryObject<TileEntityType<RegulatorBlockEntity>> REGULATOR =
            BLOCK_ENTITIES.register("regulator",
                    () -> TileEntityType.Builder.of(RegulatorBlockEntity::new, ModBlocks.REGULATOR.get()).build(null));

    public static final RegistryObject<TileEntityType<SortingMachineBlockEntity>> SORTING_MACHINE =
            BLOCK_ENTITIES.register("sorting_machine",
                    () -> TileEntityType.Builder.of(SortingMachineBlockEntity::new, ModBlocks.SORTING_MACHINE.get()).build(null));

    public static final RegistryObject<TileEntityType<IgniterBlockEntity>> IGNITER =
            BLOCK_ENTITIES.register("igniter",
                    () -> TileEntityType.Builder.of(IgniterBlockEntity::new, ModBlocks.IGNITER.get()).build(null));

    public static final RegistryObject<TileEntityType<AutoCraftingTableBlockEntity>> AUTO_CRAFTING_TABLE =
            BLOCK_ENTITIES.register("auto_crafting_table",
                    () -> TileEntityType.Builder.of(AutoCraftingTableBlockEntity::new, ModBlocks.AUTO_CRAFTING_TABLE.get()).build(null));

    public static final RegistryObject<TileEntityType<AutoCraftingTableMk2BlockEntity>> ACT_MK2 =
            BLOCK_ENTITIES.register("act_mk2",
                    () -> TileEntityType.Builder.of(AutoCraftingTableMk2BlockEntity::new, ModBlocks.ACT_MK2.get()).build(null));

    public static final RegistryObject<TileEntityType<PneumaticTubeBlockEntity>> PNEUMATIC_TUBE =
            BLOCK_ENTITIES.register("pneumatic_tube",
                    () -> TileEntityType.Builder.of(PneumaticTubeBlockEntity::new, ModBlocks.PNEUMATIC_TUBE.get()).build(null));

    public static final RegistryObject<TileEntityType<RedstoneTubeBlockEntity>> REDSTONE_TUBE =
            BLOCK_ENTITIES.register("redstone_tube",
                    () -> TileEntityType.Builder.of(RedstoneTubeBlockEntity::new, ModBlocks.REDSTONE_TUBE.get()).build(null));

    public static final RegistryObject<TileEntityType<RestrictionTubeBlockEntity>> RESTRICTION_TUBE =
            BLOCK_ENTITIES.register("restriction_tube",
                    () -> TileEntityType.Builder.of(RestrictionTubeBlockEntity::new, ModBlocks.RESTRICTION_TUBE.get()).build(null));

    private ModBlockEntities() {
    }
}
