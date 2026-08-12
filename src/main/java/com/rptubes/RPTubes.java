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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

@Mod(RPTubes.MOD_ID)
public class RPTubes {
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Version-checked handshake channel. Client and server must run the same
     * rptubes version — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     */
    public static final SimpleChannel CHANNEL;

    static {
        String protocol = ModList.get().getModContainerById(MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("dev");
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MOD_ID, "main"),
                () -> protocol,
                protocol::equals,
                protocol::equals);
    }

    public RPTubes() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);

        CHANNEL.registerMessage(0, TubeItemUpsertPacket.class,
                TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode, TubeItemUpsertPacket::handle);

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
