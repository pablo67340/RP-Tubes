package com.rptubes.registry;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.AcceleratorBlockEntity;
import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.blockentity.BlockBreakerBlockEntity;
import com.rptubes.blockentity.BufferBlockEntity;
import com.rptubes.blockentity.DeployerBlockEntity;
import com.rptubes.blockentity.EjectorBlockEntity;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.IgniterBlockEntity;
import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.blockentity.MagTubeBlockEntity;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.blockentity.RelayBlockEntity;
import com.rptubes.blockentity.RestrictionTubeBlockEntity;
import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.blockentity.RetrievulatorBlockEntity;
import com.rptubes.blockentity.SolarPanelBlockEntity;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.blockentity.ThermopileBlockEntity;
import com.rptubes.blockentity.TransposerBlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlockEntities {

    public static void register() {
        reg(AlloyFurnaceBlockEntity.class, "alloy_furnace");
        reg(TransposerBlockEntity.class, "transposer");
        reg(FilterBlockEntity.class, "filter");
        reg(EjectorBlockEntity.class, "ejector");
        reg(BlockBreakerBlockEntity.class, "block_breaker");
        reg(DeployerBlockEntity.class, "deployer");
        reg(BufferBlockEntity.class, "buffer");
        reg(ItemDetectorBlockEntity.class, "item_detector");
        reg(RelayBlockEntity.class, "relay");
        reg(RetrieverBlockEntity.class, "retriever");
        reg(RegulatorBlockEntity.class, "regulator");
        reg(SortingMachineBlockEntity.class, "sorting_machine");
        reg(IgniterBlockEntity.class, "igniter");
        reg(AutoCraftingTableBlockEntity.class, "auto_crafting_table");
        reg(AutoCraftingTableMk2BlockEntity.class, "act_mk2");
        reg(PneumaticTubeBlockEntity.class, "pneumatic_tube");
        reg(RedstoneTubeBlockEntity.class, "redstone_tube");
        reg(RestrictionTubeBlockEntity.class, "restriction_tube");
        reg(MagTubeBlockEntity.class, "mag_tube");
        reg(AcceleratorBlockEntity.class, "accelerator");
        reg(RetrievulatorBlockEntity.class, "retrievulator");
        reg(SolarPanelBlockEntity.class, "solar_panel");
        reg(ThermopileBlockEntity.class, "thermopile");
        reg(BatteryBoxBlockEntity.class, "battery_box");
    }

    private static void reg(Class<? extends net.minecraft.tileentity.TileEntity> cls, String name) {
        GameRegistry.registerTileEntity(cls, new ResourceLocation(RPTubes.MOD_ID, name));
    }

    private ModBlockEntities() {
    }
}
