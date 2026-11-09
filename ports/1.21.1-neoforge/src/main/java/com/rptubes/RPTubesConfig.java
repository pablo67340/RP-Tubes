package com.rptubes;

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

    private RPTubesConfig() {
    }
}
