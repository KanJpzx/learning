package com.kanjpz.meowski;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    // Add your real Meowski configuration options here later.

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
