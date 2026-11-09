package com.rptubes;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Optional config/rptubes.properties — defaults match 1.12.2 RP Tubes energy. */
public final class RPTubesConfig {
    public static boolean rfPowersMachines = true;
    public static boolean requireEnergy = false;
    public static int energyCapacity = 8000;
    public static int energyPerOperation = 40;

    private RPTubesConfig() {
    }

    public static void load() {
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
}
