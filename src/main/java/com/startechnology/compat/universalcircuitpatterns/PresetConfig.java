package com.startechnology.compat.universalcircuitpatterns;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

final class PresetConfig {
    private static final List<String> DEFAULT_PRESETS = List.of(
            "gtceu:circuits/ulv=kubejs:ulv_universal_circuit",
            "gtceu:circuits/lv=kubejs:lv_universal_circuit",
            "gtceu:circuits/mv=kubejs:mv_universal_circuit",
            "gtceu:circuits/hv=kubejs:hv_universal_circuit",
            "gtceu:circuits/ev=kubejs:ev_universal_circuit",
            "gtceu:circuits/iv=kubejs:iv_universal_circuit",
            "gtceu:circuits/luv=kubejs:luv_universal_circuit",
            "gtceu:circuits/zpm=kubejs:zpm_universal_circuit",
            "gtceu:circuits/uv=kubejs:uv_universal_circuit",
            "gtceu:circuits/uhv=kubejs:uhv_universal_circuit",
            "gtceu:circuits/uev=kubejs:uev_universal_circuit",
            "gtceu:circuits/uiv=kubejs:uiv_universal_circuit",
            "gtceu:circuits/uxv=kubejs:uxv_universal_circuit");

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
        SPEC = builder.build();
    }

    private PresetConfig() {}
}

