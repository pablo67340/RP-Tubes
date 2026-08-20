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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

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

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(RPTubes.MOD_ID, name), type);
    }

    public static void init() {
    }

    private ModBlockEntities() {
    }
}
