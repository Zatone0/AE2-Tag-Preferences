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
            if (!preset.isValid()) {
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
    static Preset parse(String entry) {
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

    static record Preset(TagKey<Item> tag, ResourceLocation itemId) {
        boolean isValid() {
            Item item = BuiltInRegistries.ITEM.get(itemId);
            return item != net.minecraft.world.item.Items.AIR && new ItemStack(item).is(tag);
        }
    }
}

