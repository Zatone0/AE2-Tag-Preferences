package com.startechnology.compat.universalcircuitpatterns;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;


@Mod(UniversalCircuitPatterns.MOD_ID)
public final class UniversalCircuitPatterns {
    public static final String MOD_ID = "start_universal_circuit_patterns";

    public UniversalCircuitPatterns() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PresetConfig.SPEC, "ae2-tag-preferences-client.toml");
    }

}
