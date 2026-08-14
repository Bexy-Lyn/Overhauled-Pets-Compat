package com.bexilyn.opcompat.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModServerConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue BED_CLAIM_RADIUS;
    public static final ForgeConfigSpec.DoubleValue BED_RETURN_DISTANCE;
    public static final ForgeConfigSpec.IntValue BED_SAFE_POSITION_RADIUS;

    static {

        ForgeConfigSpec.Builder builder =
                new ForgeConfigSpec.Builder();

        builder.comment(
                "Server settings for pet beds."
        );

        builder.push("pet_bed");

        BED_CLAIM_RADIUS =
                builder
                        .comment(
                                "Maximum block radius in which an unoccupied Horse Bed can be claimed by a nearby tamed horse.",
                                "Default: 10"
                        )
                        .defineInRange(
                                "claimRadius",
                                10,
                                1,
                                64
                        );

        BED_RETURN_DISTANCE =
                builder
                        .comment(
                                "At dawn, a linked horse farther away than this distance will be returned to its bed.",
                                "Default: 50.0"
                        )
                        .defineInRange(
                                "returnDistance",
                                50.0D,
                                1.0D,
                                10000.0D
                        );

        BED_SAFE_POSITION_RADIUS =
                builder
                        .comment(
                                "Maximum horizontal radius around the bed searched for a safe position when returning a horse.",
                                "Default: 4"
                        )
                        .defineInRange(
                                "safePositionRadius",
                                4,
                                0,
                                32
                        );

        builder.pop();

        SPEC = builder.build();
    }

    private ModServerConfig() {
    }
}