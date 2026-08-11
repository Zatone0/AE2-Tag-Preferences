package com.startechnology.compat.universalcircuitpatterns;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class PreferredIngredientPresets {
    private PreferredIngredientPresets() {}

    @Nullable
    public static GenericStack findPreferred(List<GenericStack> candidates) {
        for (String entry : PresetConfig.PRESETS.get()) {
            Preset preset = parse(entry);
            if (preset == null) {
                continue;
            }

            Item preferredItem = BuiltInRegistries.ITEM.get(preset.itemId());
            if (preferredItem == null
                    || preferredItem == net.minecraft.world.item.Items.AIR
                    || !new ItemStack(preferredItem).is(preset.tag())) {
                continue;
            }

            for (GenericStack candidate : candidates) {
                if (candidate.what() instanceof AEItemKey itemKey && itemKey.getItem() == preferredItem) {
                    return candidate;
                }
            }
        }

        return null;
    }

    @Nullable
    private static Preset parse(String entry) {
        int separator = entry.indexOf('=');
        if (separator <= 0 || separator != entry.lastIndexOf('=') || separator == entry.length() - 1) {
            return null;
        }

        ResourceLocation tagId = ResourceLocation.tryParse(entry.substring(0, separator).trim());
        ResourceLocation itemId = ResourceLocation.tryParse(entry.substring(separator + 1).trim());
        if (tagId == null || itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
            return null;
        }

        return new Preset(TagKey.create(BuiltInRegistries.ITEM.key(), tagId), itemId);
    }

    private record Preset(TagKey<Item> tag, ResourceLocation itemId) {}
}

