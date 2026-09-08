package com.startechnology.compat.universalcircuitpatterns;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.AEProcessingPattern;

public final class PatternAudit {
    private PatternAudit() {}

    public static Result inspect(ItemStack stack, Level level) {
        if (!PatternDetailsHelper.isEncodedPattern(stack)) return null;
        var details = PatternDetailsHelper.decodePattern(stack, level);
        if (!(details instanceof AEProcessingPattern pattern)) return null;
        var output = pattern.getPrimaryOutput();
        var presets = PresetConfig.PRESETS.get().stream()
                .map(PreferredIngredientPresets::parse)
                .filter(preset -> preset != null && preset.isValid()).toList();
        // A configured ingredient produced by this pattern belongs to a production
        // chain. Its components can legitimately be other configured ingredients
        // (for example, a lower-tier circuit), not interchangeable substitutes.
        if (output.what() instanceof AEItemKey result
                && presets.stream().anyMatch(preset -> result.isTagged(preset.tag()))) {
            return new Result(0, List.of(), output.what().getDisplayName());
        }
        int matched = 0;
        List<Mismatch> mismatches = new ArrayList<>();
        for (var input : pattern.getInputs()) {
            for (var alternative : input.getPossibleInputs()) {
                if (!(alternative.what() instanceof AEItemKey actual)) continue;
                for (var preset : presets) {
                    if (!actual.isTagged(preset.tag())) continue;
                    matched++;
                    var preferred = BuiltInRegistries.ITEM.get(preset.itemId());
                    if (actual.getItem() != preferred) {
                        var mismatch = new Mismatch(actual, AEItemKey.of(preferred), preset.tag().location().toString());
                        if (!mismatches.contains(mismatch)) mismatches.add(mismatch);
                    }
                    // Stored patterns lack the original recipe's candidate list:
                    // use the first valid tag match, and report advisory differences.
                    break;
                }
            }
        }
        return new Result(matched, List.copyOf(mismatches), output.what().getDisplayName());
    }

    public record Mismatch(AEItemKey actual, AEItemKey preferred, String tag) {
        public Component description() {
            return Component.empty().append(actual.getDisplayName()).append(" -> ")
                    .append(preferred.getDisplayName()).append(" [" + tag + "]");
        }
    }

    public record Result(int matchedInputs, List<Mismatch> mismatches, Component outputName) {
        public boolean hasPreferences() { return matchedInputs > 0; }
        public boolean passes() { return hasPreferences() && mismatches.isEmpty(); }
    }
}
