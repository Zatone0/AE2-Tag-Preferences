package com.startechnology.compat.universalcircuitpatterns;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UniversalCircuitPatterns.MOD_ID, value = Dist.CLIENT)
public final class PatternAuditTooltip {
    private PatternAuditTooltip() {}

    @SubscribeEvent
    public static void addPatternAudit(ItemTooltipEvent event) {
        if (!PresetConfig.ENABLE_AUDIT.get()) {
            return;
        }
        Level level = event.getEntity() == null ? null : event.getEntity().level();
        if (level == null) {
            return;
        }

        try {
            var result = PatternAudit.inspect(event.getItemStack(), level);
            if (result == null || !result.hasPreferences()) {
                return;
            }

            if (result.passes()) {
                event.getToolTip().add(Component.literal("Pattern audit: matches configured preferences")
                        .withStyle(ChatFormatting.GREEN));
            } else {
                event.getToolTip().add(Component.literal("Pattern audit: REVIEW - differs from preferences")
                        .withStyle(ChatFormatting.RED));
                event.getToolTip().add(Component.literal("Verify recipe alternatives before re-encoding.").withStyle(ChatFormatting.GRAY));
                result.mismatches().forEach(mismatch -> event.getToolTip().add(
                        Component.literal("  ").append(mismatch.description()).withStyle(ChatFormatting.DARK_RED)));
            }
        } catch (RuntimeException ignored) {
            // A malformed or foreign encoded pattern should retain its normal AE2 tooltip.
        }
    }
}
