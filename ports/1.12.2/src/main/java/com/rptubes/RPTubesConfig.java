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
