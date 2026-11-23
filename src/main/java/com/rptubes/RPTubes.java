package com.rptubes;

//? if forge {
/*import com.rptubes.network.TubeItemUpsertPacket;
*///?} else if fabric && >=1.21.11 && <26.1.2 {
/*import com.rptubes.item.ScrewdriverItem;
import com.rptubes.network.ModNetworking;
*///?} else if fabric {
/*import com.rptubes.compat.ProjectECompat;
import com.rptubes.item.ScrewdriverItem;
import com.rptubes.network.ModNetworking;
*///?}
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModBlocks;
import com.rptubes.registry.ModItems;
import com.rptubes.registry.ModMenus;
import com.rptubes.registry.ModRecipes;
//? if fabric && <1.21.11 {
/*import com.rptubes.registry.ModStorage;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.levelgen.GenerationStep;
*///?} else if <1.20.6 {
/*import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
*///?} else if neoforge {
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
//?} else if <1.21.11 {
/*import com.mojang.logging.LogUtils;
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
*///?} else if fabric {
/*import com.rptubes.registry.ModStorage;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.levelgen.GenerationStep;
*///?} else {
/*import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
*///?}
import org.slf4j.Logger;

//? if forgelike {
@Mod(RPTubes.MOD_ID)
public class RPTubes {
//?} else {
/*public class RPTubes implements ModInitializer {
*///?}
    public static final String MOD_ID = "rptubes";
    public static final Logger LOGGER = LogUtils.getLogger();
    //? if <1.20.1 {

    /*/^*
     * Version-checked handshake channel. Client and server must run the same
     * rptubes version — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     ^/
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RPTubesConfig.COMMON_SPEC);

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);

        CHANNEL.registerMessage(0, TubeItemUpsertPacket.class,
                TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode, TubeItemUpsertPacket::handle);

        MinecraftForge.EVENT_BUS.register(this);
    *///?} else if fabric && <1.20.6 {

    /*@Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        RPTubesConfig.load();
        ModStorage.register();
        try {
            Class.forName("team.reborn.energy.api.EnergyStorage");
            Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
        } catch (Throwable e) {
            LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
        }
        ModNetworking.register();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MOD_ID, "tin_ore")));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.getItemInHand(hand).getItem() instanceof ScrewdriverItem) {
                return ScrewdriverItem.tryRotate(player.getItemInHand(hand), level, hit.getBlockPos(), player, hand);
            }
            InteractionResult paint = com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
            if (paint.consumesAction()) {
                return paint;
            }
            return InteractionResult.PASS;
        });

        ProjectECompat.tryRegister();
    *///?} else if <1.20.6 {

    /*/^*
     * Version-checked handshake channel. Client and server must run the same
     * rptubes version — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     ^/
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RPTubesConfig.COMMON_SPEC);

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);

        CHANNEL.registerMessage(0, TubeItemUpsertPacket.class,
                TubeItemUpsertPacket::encode, TubeItemUpsertPacket::decode, TubeItemUpsertPacket::handle);

        MinecraftForge.EVENT_BUS.register(this);
    *///?} else if fabric && <1.21.1 {

    /*@Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        RPTubesConfig.load();
        try {
            Class.forName("team.reborn.energy.api.EnergyStorage");
            Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("team.reborn.energy.EnergyStorage");
                Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
            } catch (Throwable t) {
                LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
            }
        } catch (Throwable e) {
            LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
        }
        ModStorage.register();
        ModNetworking.register();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MOD_ID, "tin_ore")));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.getItemInHand(hand).getItem() instanceof ScrewdriverItem) {
                return ScrewdriverItem.tryRotate(player.getItemInHand(hand), level, hit.getBlockPos(), player, hand);
            }
            InteractionResult paint = com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
            if (paint.consumesAction()) {
                return paint;
            }
            return InteractionResult.PASS;
        });

        ProjectECompat.tryRegister();
    *///?} else if forge && <1.21.1 {

    /*/^*
     * Version-checked handshake channel. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     ^/
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
    *///?} else if fabric && <1.21.11 {

    /*/^*
     * Version-checked handshake. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     ^/
    public static final String PROTOCOL = "10200";

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        RPTubesConfig.load();
        try {
            Class.forName("team.reborn.energy.api.EnergyStorage");
            Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("team.reborn.energy.EnergyStorage");
                Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
            } catch (Throwable t) {
                LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
            }
        } catch (Throwable e) {
            LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
        }
        ModStorage.register();
        ModNetworking.register();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE,
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "tin_ore")));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.getItemInHand(hand).getItem() instanceof ScrewdriverItem) {
                return ScrewdriverItem.tryRotate(player.getItemInHand(hand), level, hit.getBlockPos(), player, hand);
            }
            InteractionResult paint = com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
            if (paint.consumesAction()) {
                return paint;
            }
            return InteractionResult.PASS;
        });

        ProjectECompat.tryRegister();
    *///?} else if forge && <1.21.11 {

    /*/^*
     * Version-checked handshake channel. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     ^/
    public static final int PROTOCOL = 10310; // 1.3.1

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"))
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
    *///?} else if fabric && <26.1.2 {

    /*/^*
     * Version-checked handshake. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     ^/
    public static final String PROTOCOL = "10200";

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        RPTubesConfig.load();
        try {
            Class.forName("team.reborn.energy.api.EnergyStorage");
            Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("team.reborn.energy.EnergyStorage");
                Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
            } catch (Throwable t) {
                LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
            }
        } catch (Throwable e) {
            LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
        }
        ModStorage.register();
        ModNetworking.register();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "tin_ore")));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.getItemInHand(hand).getItem() instanceof ScrewdriverItem) {
                return ScrewdriverItem.tryRotate(player.getItemInHand(hand), level, hit.getBlockPos(), player, hand);
            }
            InteractionResult paint = com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
            if (paint.consumesAction()) {
                return paint;
            }
            return InteractionResult.PASS;
        });

    *///?} else if fabric {

    /*/^*
     * Version-checked handshake. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     ^/
    public static final String PROTOCOL = "10200";

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        RPTubesConfig.load();
        try {
            Class.forName("team.reborn.energy.api.EnergyStorage");
            Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("team.reborn.energy.EnergyStorage");
                Class.forName("com.rptubes.compat.TrEnergyCompat").getMethod("register").invoke(null);
            } catch (Throwable t) {
                LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
            }
        } catch (Throwable e) {
            LOGGER.info("Team Reborn Energy not present; machines keep an internal buffer only.");
        }
        ModStorage.register();
        ModNetworking.register();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "tin_ore")));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.getItemInHand(hand).getItem() instanceof ScrewdriverItem) {
                return ScrewdriverItem.tryRotate(player.getItemInHand(hand), level, hit.getBlockPos(), player, hand);
            }
            InteractionResult paint = com.rptubes.tube.DyeHelper.tryUseOnTube(player, level, hand, hit);
            if (paint.consumesAction()) {
                return paint;
            }
            return InteractionResult.PASS;
        });

        ProjectECompat.tryRegister();
    *///?} else if forge {

    /*/^*
     * Version-checked handshake channel. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     ^/
    public static final int PROTOCOL = 10310; // 1.3.1

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL)
            .simpleChannel();

    public RPTubes(FMLJavaModLoadingContext context) {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, RPTubesConfig.COMMON_SPEC);
        net.minecraftforge.eventbus.api.bus.BusGroup modBus = context.getModBusGroup();

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

    *///?} else {

    /**
     * Version-checked handshake. Client and server must run the same
     * rptubes protocol — a mismatch scrambles blockstate IDs and machine behavior,
     * so refuse the connection loudly instead of corrupting silently.
     * Bump alongside any release that changes networking or machine behavior.
     */
    public static final String PROTOCOL = "10200";

    public RPTubes(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        container.registerConfig(ModConfig.Type.COMMON, RPTubesConfig.SPEC);
    //?}
        LOGGER.info("RP Tubes loading — RedPower tube logic rides again.");
    }
}
