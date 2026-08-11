package com.startechnology.compat.universalcircuitpatterns;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import appeng.api.stacks.AEItemKey;

@Mod(UniversalCircuitPatterns.MOD_ID)
public final class UniversalCircuitPatterns {
    public static final String MOD_ID = "start_universal_circuit_patterns";

    public UniversalCircuitPatterns() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PresetConfig.SPEC);
    }

    public static boolean isUniversalCircuit(AEItemKey key) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(key.getItem());
        return id.getNamespace().equals("kubejs") && id.getPath().endsWith("_universal_circuit");
    }
}
