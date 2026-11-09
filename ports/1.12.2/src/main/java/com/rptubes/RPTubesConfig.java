package com.rptubes;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** config/rptubes.cfg — worldgen toggles for packs that provide their own ores. */
@Config(modid = RPTubes.MOD_ID)
public final class RPTubesConfig {

    @Config.Name("generateTinOre")
    @Config.Comment({
            "Generate tin ore in the overworld.",
            "Disable if your pack already provides tin: every recipe accepts any",
            "ore-dictionary tin (ingotTin / oreTin), including entries added via CraftTweaker."})
    public static boolean generateTinOre = true;

    @Config.Name("generateCopperOre")
    @Config.Comment({
            "Generate copper ore in the overworld.",
            "Disable if your pack already provides copper: the brass alloy recipe accepts",
            "any ore-dictionary copper (ingotCopper)."})
    public static boolean generateCopperOre = true;

    @Config.Name("rfPowersMachines")
    @Config.Comment({
            "Filter, Retriever and Sorting Machine accept Forge Energy / RF.",
            "While they have enough energy they extract on their own (no redstone needed).",
            "Redstone pulses still work even with this off. IC2 EU and BuildCraft MJ are",
            "not spoken natively — use a converter (Flux Networks, Power Converters, etc.)."})
    public static boolean rfPowersMachines = true;

    @Config.Name("requireEnergy")
    @Config.Comment({
            "If true, Filter / Retriever / Sorting Machine will not extract on a redstone",
            "pulse unless they also have Forge Energy. Off by default so existing worlds",
            "keep working without a generator."})
    public static boolean requireEnergy = false;

    @Config.Name("energyCapacity")
    @Config.Comment("Forge Energy buffer size on Filter, Retriever and Sorting Machine.")
    @Config.RangeInt(min = 100, max = 1000000)
    public static int energyCapacity = 8000;

    @Config.Name("energyPerOperation")
    @Config.Comment("FE consumed each time a Filter / Retriever / Sorting Machine extracts.")
    @Config.RangeInt(min = 1, max = 100000)
    public static int energyPerOperation = 40;

    private RPTubesConfig() {
    }

    @Mod.EventBusSubscriber(modid = RPTubes.MOD_ID)
    private static final class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (RPTubes.MOD_ID.equals(event.getModID())) {
                ConfigManager.sync(RPTubes.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
