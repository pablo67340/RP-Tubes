package com.rptubes;

import net.minecraftforge.common.ForgeConfigSpec;

/** Common config — Forge Energy for Filter / Retriever / Sorting Machine. */
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

    private RPTubesConfig() {
    }
}
