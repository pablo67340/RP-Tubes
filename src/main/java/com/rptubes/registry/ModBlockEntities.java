package com.rptubes.registry;

import com.rptubes.RPTubes;
//? if neoforge && <1.21.11 {
import net.minecraft.core.registries.Registries;
//?}
import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.blockentity.BufferBlockEntity;
import com.rptubes.blockentity.DeployerBlockEntity;
import com.rptubes.blockentity.EjectorBlockEntity;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.blockentity.SolarPanelBlockEntity;
import com.rptubes.blockentity.ThermopileBlockEntity;
import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.blockentity.RelayBlockEntity;
import com.rptubes.blockentity.RestrictionTubeBlockEntity;
import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.blockentity.TransposerBlockEntity;
//? if fabric && >=1.21.1 && <1.21.11 {
/*import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE =
            register("alloy_furnace", BlockEntityType.Builder.of(AlloyFurnaceBlockEntity::new, ModBlocks.ALLOY_FURNACE).build(null));
    public static final BlockEntityType<TransposerBlockEntity> TRANSPOSER =
            register("transposer", BlockEntityType.Builder.of(TransposerBlockEntity::new, ModBlocks.TRANSPOSER).build(null));
    public static final BlockEntityType<FilterBlockEntity> FILTER =
            register("filter", BlockEntityType.Builder.of(FilterBlockEntity::new, ModBlocks.FILTER).build(null));
    public static final BlockEntityType<EjectorBlockEntity> EJECTOR =
            register("ejector", BlockEntityType.Builder.of(EjectorBlockEntity::new, ModBlocks.EJECTOR).build(null));
    public static final BlockEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER =
            register("block_breaker", BlockEntityType.Builder.of(BlockBreakerBlockEntity::new, ModBlocks.BLOCK_BREAKER).build(null));
    public static final BlockEntityType<DeployerBlockEntity> DEPLOYER =
            register("deployer", BlockEntityType.Builder.of(DeployerBlockEntity::new, ModBlocks.DEPLOYER).build(null));
    public static final BlockEntityType<BufferBlockEntity> BUFFER =
            register("buffer", BlockEntityType.Builder.of(BufferBlockEntity::new, ModBlocks.BUFFER).build(null));
    public static final BlockEntityType<ItemDetectorBlockEntity> ITEM_DETECTOR =
            register("item_detector", BlockEntityType.Builder.of(ItemDetectorBlockEntity::new, ModBlocks.ITEM_DETECTOR).build(null));
    public static final BlockEntityType<RelayBlockEntity> RELAY =
            register("relay", BlockEntityType.Builder.of(RelayBlockEntity::new, ModBlocks.RELAY).build(null));
    public static final BlockEntityType<RetrieverBlockEntity> RETRIEVER =
            register("retriever", BlockEntityType.Builder.of(RetrieverBlockEntity::new, ModBlocks.RETRIEVER).build(null));
    public static final BlockEntityType<RegulatorBlockEntity> REGULATOR =
            register("regulator", BlockEntityType.Builder.of(RegulatorBlockEntity::new, ModBlocks.REGULATOR).build(null));
    public static final BlockEntityType<SortingMachineBlockEntity> SORTING_MACHINE =
            register("sorting_machine", BlockEntityType.Builder.of(SortingMachineBlockEntity::new, ModBlocks.SORTING_MACHINE).build(null));
    public static final BlockEntityType<IgniterBlockEntity> IGNITER =
            register("igniter", BlockEntityType.Builder.of(IgniterBlockEntity::new, ModBlocks.IGNITER).build(null));
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL =
            register("solar_panel", BlockEntityType.Builder.of(SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL).build(null));
    public static final BlockEntityType<ThermopileBlockEntity> THERMOPILE =
            register("thermopile", BlockEntityType.Builder.of(ThermopileBlockEntity::new, ModBlocks.THERMOPILE).build(null));
    public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX =
            register("battery_box", BlockEntityType.Builder.of(BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX).build(null));
    public static final BlockEntityType<AutoCraftingTableBlockEntity> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", BlockEntityType.Builder.of(AutoCraftingTableBlockEntity::new, ModBlocks.AUTO_CRAFTING_TABLE).build(null));
    public static final BlockEntityType<AutoCraftingTableMk2BlockEntity> ACT_MK2 =
            register("act_mk2", BlockEntityType.Builder.of(AutoCraftingTableMk2BlockEntity::new, ModBlocks.ACT_MK2).build(null));
    public static final BlockEntityType<PneumaticTubeBlockEntity> PNEUMATIC_TUBE =
            register("pneumatic_tube", BlockEntityType.Builder.of(PneumaticTubeBlockEntity::new, ModBlocks.PNEUMATIC_TUBE).build(null));
    public static final BlockEntityType<RedstoneTubeBlockEntity> REDSTONE_TUBE =
            register("redstone_tube", BlockEntityType.Builder.of(RedstoneTubeBlockEntity::new, ModBlocks.REDSTONE_TUBE).build(null));
    public static final BlockEntityType<RestrictionTubeBlockEntity> RESTRICTION_TUBE =
            register("restriction_tube", BlockEntityType.Builder.of(RestrictionTubeBlockEntity::new, ModBlocks.RESTRICTION_TUBE).build(null));

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
*///?} else if fabric && >=1.21.1 && <26.1.2 {
/*import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL =
            register("solar_panel", new BlockEntityType<>(SolarPanelBlockEntity::new, Set.of(ModBlocks.SOLAR_PANEL)));
    public static final BlockEntityType<ThermopileBlockEntity> THERMOPILE =
            register("thermopile", new BlockEntityType<>(ThermopileBlockEntity::new, Set.of(ModBlocks.THERMOPILE)));
    public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX =
            register("battery_box", new BlockEntityType<>(BatteryBoxBlockEntity::new, Set.of(ModBlocks.BATTERY_BOX)));
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
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
*///?} else if fabric && <1.21.1 {
/*import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE =
            register("alloy_furnace", BlockEntityType.Builder.of(AlloyFurnaceBlockEntity::new, ModBlocks.ALLOY_FURNACE).build(null));
    public static final BlockEntityType<TransposerBlockEntity> TRANSPOSER =
            register("transposer", BlockEntityType.Builder.of(TransposerBlockEntity::new, ModBlocks.TRANSPOSER).build(null));
    public static final BlockEntityType<FilterBlockEntity> FILTER =
            register("filter", BlockEntityType.Builder.of(FilterBlockEntity::new, ModBlocks.FILTER).build(null));
    public static final BlockEntityType<EjectorBlockEntity> EJECTOR =
            register("ejector", BlockEntityType.Builder.of(EjectorBlockEntity::new, ModBlocks.EJECTOR).build(null));
    public static final BlockEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER =
            register("block_breaker", BlockEntityType.Builder.of(BlockBreakerBlockEntity::new, ModBlocks.BLOCK_BREAKER).build(null));
    public static final BlockEntityType<DeployerBlockEntity> DEPLOYER =
            register("deployer", BlockEntityType.Builder.of(DeployerBlockEntity::new, ModBlocks.DEPLOYER).build(null));
    public static final BlockEntityType<BufferBlockEntity> BUFFER =
            register("buffer", BlockEntityType.Builder.of(BufferBlockEntity::new, ModBlocks.BUFFER).build(null));
    public static final BlockEntityType<ItemDetectorBlockEntity> ITEM_DETECTOR =
            register("item_detector", BlockEntityType.Builder.of(ItemDetectorBlockEntity::new, ModBlocks.ITEM_DETECTOR).build(null));
    public static final BlockEntityType<RelayBlockEntity> RELAY =
            register("relay", BlockEntityType.Builder.of(RelayBlockEntity::new, ModBlocks.RELAY).build(null));
    public static final BlockEntityType<RetrieverBlockEntity> RETRIEVER =
            register("retriever", BlockEntityType.Builder.of(RetrieverBlockEntity::new, ModBlocks.RETRIEVER).build(null));
    public static final BlockEntityType<RegulatorBlockEntity> REGULATOR =
            register("regulator", BlockEntityType.Builder.of(RegulatorBlockEntity::new, ModBlocks.REGULATOR).build(null));
    public static final BlockEntityType<SortingMachineBlockEntity> SORTING_MACHINE =
            register("sorting_machine", BlockEntityType.Builder.of(SortingMachineBlockEntity::new, ModBlocks.SORTING_MACHINE).build(null));
    public static final BlockEntityType<IgniterBlockEntity> IGNITER =
            register("igniter", BlockEntityType.Builder.of(IgniterBlockEntity::new, ModBlocks.IGNITER).build(null));
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL =
            register("solar_panel", BlockEntityType.Builder.of(SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL).build(null));
    public static final BlockEntityType<ThermopileBlockEntity> THERMOPILE =
            register("thermopile", BlockEntityType.Builder.of(ThermopileBlockEntity::new, ModBlocks.THERMOPILE).build(null));
    public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX =
            register("battery_box", BlockEntityType.Builder.of(BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX).build(null));
    public static final BlockEntityType<AutoCraftingTableBlockEntity> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", BlockEntityType.Builder.of(AutoCraftingTableBlockEntity::new, ModBlocks.AUTO_CRAFTING_TABLE).build(null));
    public static final BlockEntityType<AutoCraftingTableMk2BlockEntity> ACT_MK2 =
            register("act_mk2", BlockEntityType.Builder.of(AutoCraftingTableMk2BlockEntity::new, ModBlocks.ACT_MK2).build(null));
    public static final BlockEntityType<PneumaticTubeBlockEntity> PNEUMATIC_TUBE =
            register("pneumatic_tube", BlockEntityType.Builder.of(PneumaticTubeBlockEntity::new, ModBlocks.PNEUMATIC_TUBE).build(null));
    public static final BlockEntityType<RedstoneTubeBlockEntity> REDSTONE_TUBE =
            register("redstone_tube", BlockEntityType.Builder.of(RedstoneTubeBlockEntity::new, ModBlocks.REDSTONE_TUBE).build(null));
    public static final BlockEntityType<RestrictionTubeBlockEntity> RESTRICTION_TUBE =
            register("restriction_tube", BlockEntityType.Builder.of(RestrictionTubeBlockEntity::new, ModBlocks.RESTRICTION_TUBE).build(null));

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }
*///?} else if neoforge && <1.21.11 {
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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel",
                    () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThermopileBlockEntity>> THERMOPILE =
            BLOCK_ENTITIES.register("thermopile",
                    () -> BlockEntityType.Builder.of(ThermopileBlockEntity::new, ModBlocks.THERMOPILE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX =
            BLOCK_ENTITIES.register("battery_box",
                    () -> BlockEntityType.Builder.of(BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX.get()).build(null));

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
//?} else if fabric {
/*import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE =
            register("alloy_furnace", AlloyFurnaceBlockEntity::new, ModBlocks.ALLOY_FURNACE);
    public static final BlockEntityType<TransposerBlockEntity> TRANSPOSER =
            register("transposer", TransposerBlockEntity::new, ModBlocks.TRANSPOSER);
    public static final BlockEntityType<FilterBlockEntity> FILTER =
            register("filter", FilterBlockEntity::new, ModBlocks.FILTER);
    public static final BlockEntityType<EjectorBlockEntity> EJECTOR =
            register("ejector", EjectorBlockEntity::new, ModBlocks.EJECTOR);
    public static final BlockEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER =
            register("block_breaker", BlockBreakerBlockEntity::new, ModBlocks.BLOCK_BREAKER);
    public static final BlockEntityType<DeployerBlockEntity> DEPLOYER =
            register("deployer", DeployerBlockEntity::new, ModBlocks.DEPLOYER);
    public static final BlockEntityType<BufferBlockEntity> BUFFER =
            register("buffer", BufferBlockEntity::new, ModBlocks.BUFFER);
    public static final BlockEntityType<ItemDetectorBlockEntity> ITEM_DETECTOR =
            register("item_detector", ItemDetectorBlockEntity::new, ModBlocks.ITEM_DETECTOR);
    public static final BlockEntityType<RelayBlockEntity> RELAY =
            register("relay", RelayBlockEntity::new, ModBlocks.RELAY);
    public static final BlockEntityType<RetrieverBlockEntity> RETRIEVER =
            register("retriever", RetrieverBlockEntity::new, ModBlocks.RETRIEVER);
    public static final BlockEntityType<RegulatorBlockEntity> REGULATOR =
            register("regulator", RegulatorBlockEntity::new, ModBlocks.REGULATOR);
    public static final BlockEntityType<SortingMachineBlockEntity> SORTING_MACHINE =
            register("sorting_machine", SortingMachineBlockEntity::new, ModBlocks.SORTING_MACHINE);
    public static final BlockEntityType<IgniterBlockEntity> IGNITER =
            register("igniter", IgniterBlockEntity::new, ModBlocks.IGNITER);
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL =
            register("solar_panel", SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL);
    public static final BlockEntityType<ThermopileBlockEntity> THERMOPILE =
            register("thermopile", ThermopileBlockEntity::new, ModBlocks.THERMOPILE);
    public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX =
            register("battery_box", BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX);
    public static final BlockEntityType<AutoCraftingTableBlockEntity> AUTO_CRAFTING_TABLE =
            register("auto_crafting_table", AutoCraftingTableBlockEntity::new, ModBlocks.AUTO_CRAFTING_TABLE);
    public static final BlockEntityType<AutoCraftingTableMk2BlockEntity> ACT_MK2 =
            register("act_mk2", AutoCraftingTableMk2BlockEntity::new, ModBlocks.ACT_MK2);
    public static final BlockEntityType<PneumaticTubeBlockEntity> PNEUMATIC_TUBE =
            register("pneumatic_tube", PneumaticTubeBlockEntity::new, ModBlocks.PNEUMATIC_TUBE);
    public static final BlockEntityType<RedstoneTubeBlockEntity> REDSTONE_TUBE =
            register("redstone_tube", RedstoneTubeBlockEntity::new, ModBlocks.REDSTONE_TUBE);
    public static final BlockEntityType<RestrictionTubeBlockEntity> RESTRICTION_TUBE =
            register("restriction_tube", RestrictionTubeBlockEntity::new, ModBlocks.RESTRICTION_TUBE);

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(
            String name, FabricBlockEntityTypeBuilder.Factory<? extends T> factory,
            net.minecraft.world.level.block.Block block) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, name),
                FabricBlockEntityTypeBuilder.<T>create(factory, block).build());
    }

    public static void init() {
    }
*///?} else if forge && >=1.21.11 {
/*import net.minecraft.world.level.block.entity.BlockEntityType;
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
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel",
                    () -> new BlockEntityType<>(SolarPanelBlockEntity::new, java.util.Set.of(ModBlocks.SOLAR_PANEL.get())));
    public static final RegistryObject<BlockEntityType<ThermopileBlockEntity>> THERMOPILE =
            BLOCK_ENTITIES.register("thermopile",
                    () -> new BlockEntityType<>(ThermopileBlockEntity::new, java.util.Set.of(ModBlocks.THERMOPILE.get())));
    public static final RegistryObject<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX =
            BLOCK_ENTITIES.register("battery_box",
                    () -> new BlockEntityType<>(BatteryBoxBlockEntity::new, java.util.Set.of(ModBlocks.BATTERY_BOX.get())));

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
*///?} else if >=1.21.11 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RPTubes.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE =
            BLOCK_ENTITIES.register("alloy_furnace",
                    () -> new BlockEntityType<>(AlloyFurnaceBlockEntity::new, java.util.Set.of(ModBlocks.ALLOY_FURNACE.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransposerBlockEntity>> TRANSPOSER =
            BLOCK_ENTITIES.register("transposer",
                    () -> new BlockEntityType<>(TransposerBlockEntity::new, java.util.Set.of(ModBlocks.TRANSPOSER.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FilterBlockEntity>> FILTER =
            BLOCK_ENTITIES.register("filter",
                    () -> new BlockEntityType<>(FilterBlockEntity::new, java.util.Set.of(ModBlocks.FILTER.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EjectorBlockEntity>> EJECTOR =
            BLOCK_ENTITIES.register("ejector",
                    () -> new BlockEntityType<>(EjectorBlockEntity::new, java.util.Set.of(ModBlocks.EJECTOR.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockBreakerBlockEntity>> BLOCK_BREAKER =
            BLOCK_ENTITIES.register("block_breaker",
                    () -> new BlockEntityType<>(BlockBreakerBlockEntity::new, java.util.Set.of(ModBlocks.BLOCK_BREAKER.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeployerBlockEntity>> DEPLOYER =
            BLOCK_ENTITIES.register("deployer",
                    () -> new BlockEntityType<>(DeployerBlockEntity::new, java.util.Set.of(ModBlocks.DEPLOYER.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BufferBlockEntity>> BUFFER =
            BLOCK_ENTITIES.register("buffer",
                    () -> new BlockEntityType<>(BufferBlockEntity::new, java.util.Set.of(ModBlocks.BUFFER.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDetectorBlockEntity>> ITEM_DETECTOR =
            BLOCK_ENTITIES.register("item_detector",
                    () -> new BlockEntityType<>(ItemDetectorBlockEntity::new, java.util.Set.of(ModBlocks.ITEM_DETECTOR.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RelayBlockEntity>> RELAY =
            BLOCK_ENTITIES.register("relay",
                    () -> new BlockEntityType<>(RelayBlockEntity::new, java.util.Set.of(ModBlocks.RELAY.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RetrieverBlockEntity>> RETRIEVER =
            BLOCK_ENTITIES.register("retriever",
                    () -> new BlockEntityType<>(RetrieverBlockEntity::new, java.util.Set.of(ModBlocks.RETRIEVER.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RegulatorBlockEntity>> REGULATOR =
            BLOCK_ENTITIES.register("regulator",
                    () -> new BlockEntityType<>(RegulatorBlockEntity::new, java.util.Set.of(ModBlocks.REGULATOR.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SortingMachineBlockEntity>> SORTING_MACHINE =
            BLOCK_ENTITIES.register("sorting_machine",
                    () -> new BlockEntityType<>(SortingMachineBlockEntity::new, java.util.Set.of(ModBlocks.SORTING_MACHINE.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IgniterBlockEntity>> IGNITER =
            BLOCK_ENTITIES.register("igniter",
                    () -> new BlockEntityType<>(IgniterBlockEntity::new, java.util.Set.of(ModBlocks.IGNITER.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel",
                    () -> new BlockEntityType<>(SolarPanelBlockEntity::new, java.util.Set.of(ModBlocks.SOLAR_PANEL.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThermopileBlockEntity>> THERMOPILE =
            BLOCK_ENTITIES.register("thermopile",
                    () -> new BlockEntityType<>(ThermopileBlockEntity::new, java.util.Set.of(ModBlocks.THERMOPILE.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX =
            BLOCK_ENTITIES.register("battery_box",
                    () -> new BlockEntityType<>(BatteryBoxBlockEntity::new, java.util.Set.of(ModBlocks.BATTERY_BOX.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoCraftingTableBlockEntity>> AUTO_CRAFTING_TABLE =
            BLOCK_ENTITIES.register("auto_crafting_table",
                    () -> new BlockEntityType<>(AutoCraftingTableBlockEntity::new, java.util.Set.of(ModBlocks.AUTO_CRAFTING_TABLE.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoCraftingTableMk2BlockEntity>> ACT_MK2 =
            BLOCK_ENTITIES.register("act_mk2",
                    () -> new BlockEntityType<>(AutoCraftingTableMk2BlockEntity::new, java.util.Set.of(ModBlocks.ACT_MK2.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PneumaticTubeBlockEntity>> PNEUMATIC_TUBE =
            BLOCK_ENTITIES.register("pneumatic_tube",
                    () -> new BlockEntityType<>(PneumaticTubeBlockEntity::new, java.util.Set.of(ModBlocks.PNEUMATIC_TUBE.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneTubeBlockEntity>> REDSTONE_TUBE =
            BLOCK_ENTITIES.register("redstone_tube",
                    () -> new BlockEntityType<>(RedstoneTubeBlockEntity::new, java.util.Set.of(ModBlocks.REDSTONE_TUBE.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RestrictionTubeBlockEntity>> RESTRICTION_TUBE =
            BLOCK_ENTITIES.register("restriction_tube",
                    () -> new BlockEntityType<>(RestrictionTubeBlockEntity::new, java.util.Set.of(ModBlocks.RESTRICTION_TUBE.get())));
*///?} else {
/*import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RPTubes.MOD_ID);

    public static final RegistryObject<BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE =
            BLOCK_ENTITIES.register("alloy_furnace",
                    () -> BlockEntityType.Builder.of(AlloyFurnaceBlockEntity::new, ModBlocks.ALLOY_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TransposerBlockEntity>> TRANSPOSER =
            BLOCK_ENTITIES.register("transposer",
                    () -> BlockEntityType.Builder.of(TransposerBlockEntity::new, ModBlocks.TRANSPOSER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FilterBlockEntity>> FILTER =
            BLOCK_ENTITIES.register("filter",
                    () -> BlockEntityType.Builder.of(FilterBlockEntity::new, ModBlocks.FILTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<EjectorBlockEntity>> EJECTOR =
            BLOCK_ENTITIES.register("ejector",
                    () -> BlockEntityType.Builder.of(EjectorBlockEntity::new, ModBlocks.EJECTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockBreakerBlockEntity>> BLOCK_BREAKER =
            BLOCK_ENTITIES.register("block_breaker",
                    () -> BlockEntityType.Builder.of(BlockBreakerBlockEntity::new, ModBlocks.BLOCK_BREAKER.get()).build(null));

    public static final RegistryObject<BlockEntityType<DeployerBlockEntity>> DEPLOYER =
            BLOCK_ENTITIES.register("deployer",
                    () -> BlockEntityType.Builder.of(DeployerBlockEntity::new, ModBlocks.DEPLOYER.get()).build(null));

    public static final RegistryObject<BlockEntityType<BufferBlockEntity>> BUFFER =
            BLOCK_ENTITIES.register("buffer",
                    () -> BlockEntityType.Builder.of(BufferBlockEntity::new, ModBlocks.BUFFER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ItemDetectorBlockEntity>> ITEM_DETECTOR =
            BLOCK_ENTITIES.register("item_detector",
                    () -> BlockEntityType.Builder.of(ItemDetectorBlockEntity::new, ModBlocks.ITEM_DETECTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<RelayBlockEntity>> RELAY =
            BLOCK_ENTITIES.register("relay",
                    () -> BlockEntityType.Builder.of(RelayBlockEntity::new, ModBlocks.RELAY.get()).build(null));

    public static final RegistryObject<BlockEntityType<RetrieverBlockEntity>> RETRIEVER =
            BLOCK_ENTITIES.register("retriever",
                    () -> BlockEntityType.Builder.of(RetrieverBlockEntity::new, ModBlocks.RETRIEVER.get()).build(null));

    public static final RegistryObject<BlockEntityType<RegulatorBlockEntity>> REGULATOR =
            BLOCK_ENTITIES.register("regulator",
                    () -> BlockEntityType.Builder.of(RegulatorBlockEntity::new, ModBlocks.REGULATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<SortingMachineBlockEntity>> SORTING_MACHINE =
            BLOCK_ENTITIES.register("sorting_machine",
                    () -> BlockEntityType.Builder.of(SortingMachineBlockEntity::new, ModBlocks.SORTING_MACHINE.get()).build(null));

    public static final RegistryObject<BlockEntityType<IgniterBlockEntity>> IGNITER =
            BLOCK_ENTITIES.register("igniter",
                    () -> BlockEntityType.Builder.of(IgniterBlockEntity::new, ModBlocks.IGNITER.get()).build(null));
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel",
                    () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<ThermopileBlockEntity>> THERMOPILE =
            BLOCK_ENTITIES.register("thermopile",
                    () -> BlockEntityType.Builder.of(ThermopileBlockEntity::new, ModBlocks.THERMOPILE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX =
            BLOCK_ENTITIES.register("battery_box",
                    () -> BlockEntityType.Builder.of(BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX.get()).build(null));

    public static final RegistryObject<BlockEntityType<AutoCraftingTableBlockEntity>> AUTO_CRAFTING_TABLE =
            BLOCK_ENTITIES.register("auto_crafting_table",
                    () -> BlockEntityType.Builder.of(AutoCraftingTableBlockEntity::new, ModBlocks.AUTO_CRAFTING_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AutoCraftingTableMk2BlockEntity>> ACT_MK2 =
            BLOCK_ENTITIES.register("act_mk2",
                    () -> BlockEntityType.Builder.of(AutoCraftingTableMk2BlockEntity::new, ModBlocks.ACT_MK2.get()).build(null));

    public static final RegistryObject<BlockEntityType<PneumaticTubeBlockEntity>> PNEUMATIC_TUBE =
            BLOCK_ENTITIES.register("pneumatic_tube",
                    () -> BlockEntityType.Builder.of(PneumaticTubeBlockEntity::new, ModBlocks.PNEUMATIC_TUBE.get()).build(null));

    public static final RegistryObject<BlockEntityType<RedstoneTubeBlockEntity>> REDSTONE_TUBE =
            BLOCK_ENTITIES.register("redstone_tube",
                    () -> BlockEntityType.Builder.of(RedstoneTubeBlockEntity::new, ModBlocks.REDSTONE_TUBE.get()).build(null));

    public static final RegistryObject<BlockEntityType<RestrictionTubeBlockEntity>> RESTRICTION_TUBE =
            BLOCK_ENTITIES.register("restriction_tube",
                    () -> BlockEntityType.Builder.of(RestrictionTubeBlockEntity::new, ModBlocks.RESTRICTION_TUBE.get()).build(null));
*///?}

    private ModBlockEntities() {
    }
}
