package com.startechnology.compat.universalcircuitpatterns;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.AEProcessingPattern;

public final class PatternAudit {
    private static final List<TagKey<Item>> CIRCUIT_TAGS = List.of(
            circuitTag("ulv"), circuitTag("lv"), circuitTag("mv"), circuitTag("hv"), circuitTag("ev"),
            circuitTag("iv"), circuitTag("luv"), circuitTag("zpm"), circuitTag("uv"), circuitTag("uhv"),
            circuitTag("uev"), circuitTag("uiv"), circuitTag("uxv"));

    private PatternAudit() {}

    public static Result inspect(ItemStack stack, Level level) {
        if (!PatternDetailsHelper.isEncodedPattern(stack)) {
            return null;
        }

        var details = PatternDetailsHelper.decodePattern(stack, level);
        if (!(details instanceof AEProcessingPattern processingPattern)) {
            return null;
        }

        var primaryOutput = processingPattern.getPrimaryOutput();
        Component outputName = primaryOutput.what().getDisplayName();
        if (primaryOutput.what() instanceof AEItemKey outputKey && isTieredCircuit(outputKey)) {
            // Circuit production chains intentionally consume predecessor circuits as components.
            // Universal Circuit recipes are part of this same category.
            return new Result(List.of(), List.of(), outputName);
        }

        List<AEItemKey> circuits = new ArrayList<>();
        for (var input : processingPattern.getInputs()) {
            for (var possibleInput : input.getPossibleInputs()) {
                if (possibleInput.what() instanceof AEItemKey itemKey && isTieredCircuit(itemKey)) {
                    circuits.add(itemKey);
                }
            }
        }

        List<AEItemKey> conventionalCircuits = circuits.stream()
                .filter(key -> !UniversalCircuitPatterns.isUniversalCircuit(key))
                .toList();
        return new Result(circuits, conventionalCircuits, outputName);
    }

    private static boolean isTieredCircuit(AEItemKey key) {
        return CIRCUIT_TAGS.stream().anyMatch(key::isTagged);
    }

    private static TagKey<Item> circuitTag(String tier) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("gtceu", "circuits/" + tier));
    }

    public record Result(List<AEItemKey> circuits, List<AEItemKey> conventionalCircuits, Component outputName) {
        public boolean hasCircuits() {
            return !circuits.isEmpty();
        }

        public boolean passes() {
            return hasCircuits() && conventionalCircuits.isEmpty();
        }
    }
}
