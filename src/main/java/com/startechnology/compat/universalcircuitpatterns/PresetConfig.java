package com.startechnology.compat.universalcircuitpatterns;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

final class PresetConfig {
    private static final List<String> DEFAULT_PRESETS = List.of();

    static final ForgeConfigSpec.BooleanValue ENABLE_AUDIT;
    static final ForgeConfigSpec SPEC;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> PRESETS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        PRESETS = builder
                .comment(
                        "Ordered AE2 processing-pattern ingredient preferences.",
                        "Format: item_tag=preferred_item (resource locations without #).",
                        "The item must belong to the tag and be a candidate for the recipe slot.",
                        "The first matching entry wins; invalid entries are ignored.")
                .defineListAllowEmpty("presets", DEFAULT_PRESETS, value -> value instanceof String);
        ENABLE_AUDIT = builder
                .comment("Audit processing patterns against your configured tag preferences.",
                        "Differences are advisory: verify recipe compatibility before re-encoding.")
                .define("enableAudit", false);
        SPEC = builder.build();
    }

    private PresetConfig() {}
}

