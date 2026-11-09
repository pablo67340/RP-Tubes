package com.rptubes;

import com.rptubes.network.TubeItemUpsertPacket;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.registry.ModRecipes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import org.slf4j.Logger;

@Mod(RPTubes.MOD_ID)
public class RPTubes {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Version-checked handshake channel. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     */
    public static final int PROTOCOL = 10310; // 1.3.1

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(new ResourceLocation(MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL)
            .simpleChannel();

    public RPTubes() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RPTubesConfig.COMMON_SPEC);

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);

        CHANNEL.messageBuilder(TubeItemUpsertPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(TubeItemUpsertPacket::encode)
                .decoder(TubeItemUpsertPacket::decode)
                .consumerMainThread(TubeItemUpsertPacket::handle)
                .add();

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
