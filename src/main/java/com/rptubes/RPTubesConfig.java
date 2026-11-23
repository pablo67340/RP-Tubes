package com.rptubes;

//? if forge {
/*import net.minecraftforge.common.ForgeConfigSpec;

/^* Common config — Forge Energy for Filter / Retriever / Sorting Machine. ^/
public final class RPTubesConfig {
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ForgeConfigSpec.BooleanValue rfPowersMachines;
    public static final ForgeConfigSpec.BooleanValue requireEnergy;
    public static final ForgeConfigSpec.IntValue energyCapacity;
    public static final ForgeConfigSpec.IntValue energyPerOperation;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Forge Energy / RF for Filter, Retriever and Sorting Machine.");
        rfPowersMachines = builder
                .comment("Filter, Retriever and Sorting Machine accept Forge Energy / RF.",
                        "While they have enough energy they extract on their own (no redstone needed).",
                        "Redstone pulses still work even with this off.")
                .define("rfPowersMachines", true);
        requireEnergy = builder
                .comment("If true, Filter / Retriever / Sorting Machine will not extract on a redstone",
                        "pulse unless they also have Forge Energy.")
                .define("requireEnergy", false);
        energyCapacity = builder
                .comment("Forge Energy buffer size on Filter, Retriever and Sorting Machine.")
                .defineInRange("energyCapacity", 8000, 100, 1_000_000);
        energyPerOperation = builder
                .comment("FE consumed each time a Filter / Retriever / Sorting Machine extracts.")
                .defineInRange("energyPerOperation", 40, 1, 100_000);
        COMMON_SPEC = builder.build();
    }
*///?} else if fabric {
/*import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/^* Optional config/rptubes.properties — defaults match 1.12.2 RP Tubes energy. ^/
public final class RPTubesConfig {
    public static boolean rfPowersMachines = true;
    public static boolean requireEnergy = false;
    public static int energyCapacity = 8000;
    public static int energyPerOperation = 40;
*///?} else {
import net.neoforged.neoforge.common.ModConfigSpec;

public final class RPTubesConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue rfPowersMachines;
    public static final ModConfigSpec.BooleanValue requireEnergy;
    public static final ModConfigSpec.IntValue energyCapacity;
    public static final ModConfigSpec.IntValue energyPerOperation;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        rfPowersMachines = builder
                .comment("Filter, Retriever and Sorting Machine accept Forge Energy / RF.",
                        "While they have enough energy they extract on their own (no redstone needed).",
                        "Redstone pulses still work even with this off.")
                .define("rfPowersMachines", true);
        requireEnergy = builder
                .comment("If true, Filter / Retriever / Sorting Machine will not extract on a redstone",
                        "pulse unless they also have Forge Energy. Off by default so existing worlds",
                        "keep working without a generator.")
                .define("requireEnergy", false);
        energyCapacity = builder
                .comment("Forge Energy buffer size on Filter, Retriever and Sorting Machine.")
                .defineInRange("energyCapacity", 8000, 100, 1_000_000);
        energyPerOperation = builder
                .comment("FE consumed each time a Filter / Retriever / Sorting Machine extracts.")
                .defineInRange("energyPerOperation", 40, 1, 100_000);
        SPEC = builder.build();
    }
//?}

    private RPTubesConfig() {
    }
    //? if fabric {

    /*public static void load() {
        Path path = Path.of("config", "rptubes.properties");
        if (!Files.exists(path)) {
            return;
        }
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            props.load(reader);
            rfPowersMachines = Boolean.parseBoolean(props.getProperty("rfPowersMachines", "true"));
            requireEnergy = Boolean.parseBoolean(props.getProperty("requireEnergy", "false"));
            energyCapacity = parseInt(props.getProperty("energyCapacity"), energyCapacity, 100, 1_000_000);
            energyPerOperation = parseInt(props.getProperty("energyPerOperation"), energyPerOperation, 1, 100_000);
        } catch (Exception e) {
            RPTubes.LOGGER.warn("Failed to load config/rptubes.properties", e);
        }
    }

    private static int parseInt(String raw, int fallback, int min, int max) {
        if (raw == null) {
            return fallback;
        }
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(raw.trim())));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
    *///?}
}
