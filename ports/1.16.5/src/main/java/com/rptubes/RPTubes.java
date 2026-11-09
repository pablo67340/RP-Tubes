package com.rptubes;

import com.rptubes.network.TubeItemUpsertPacket;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.registry.ModRecipes;
import com.rptubes.worldgen.OreGen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RPTubes.MOD_ID)
public class RPTubes {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Version-checked handshake channel. Client and server must run the same
     * rptubes version — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     */
    public static SimpleChannel CHANNEL;

    public RPTubes() {
        // 1.16: ModList is not queryable during <clinit>, so build the channel here
        // where the active container is this mod.
        String protocol = ModLoadingContext.get().getActiveContainer()
                .getModInfo().getVersion().toString();
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MOD_ID, "main"),
                () -> protocol,
                protocol::equals,
                protocol::equals);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RPTubesConfig.COMMON_SPEC);

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        modBus.addListener(this::commonSetup);

        CHANNEL.registerMessage(0, TubeItemUpsertPacket.class,
                TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode, TubeItemUpsertPacket::handle);

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(OreGen::registerFeatures);
    }
}
