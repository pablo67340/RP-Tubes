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
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
                Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, name),
                FabricBlockEntityTypeBuilder.<T>create(factory, block).build());
    }

    public static void init() {
    }

    private ModBlockEntities() {
    }
}
