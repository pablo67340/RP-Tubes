package com.rptubes;

import com.rptubes.network.GuiHandler;
import com.rptubes.network.TubeItemUpsertPacket;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModRecipes;
import com.rptubes.worldgen.OreGen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = RPTubes.MOD_ID, name = "RP Tubes", version = "1.3.1",
        acceptedMinecraftVersions = "[1.12,1.13)")
public class RPTubes {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static RPTubes instance;

    public static SimpleNetworkWrapper CHANNEL;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        CHANNEL.registerMessage(TubeItemUpsertPacket.Handler.class, TubeItemUpsertPacket.class, 0, Side.CLIENT);

        ModBlockEntities.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        GameRegistry.registerWorldGenerator(new OreGen(), 0);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // after all mods have registered ore dictionary entries
        ModRecipes.register();
        com.rptubes.compat.ProjectECompat.init();
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
