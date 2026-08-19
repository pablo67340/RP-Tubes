package com.rptubes;

import com.rptubes.network.GuiHandler;
import com.rptubes.network.TubeItemUpsertPacket;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModRecipes;
import com.rptubes.worldgen.OreGen;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = RPTubes.MOD_ID, name = "RP Tubes", version = "1.2.0",
        acceptedMinecraftVersions = "[1.7.10]")
public class RPTubes {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static RPTubes instance;

    @SidedProxy(clientSide = "com.rptubes.client.ClientProxy", serverSide = "com.rptubes.CommonProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper CHANNEL;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        CHANNEL.registerMessage(TubeItemUpsertPacket.Handler.class, TubeItemUpsertPacket.class, 0, Side.CLIENT);

        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        GameRegistry.registerWorldGenerator(new OreGen(), 0);
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // after all mods have registered ore dictionary entries
        ModRecipes.register();
        com.rptubes.compat.ProjectECompat.init();
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
