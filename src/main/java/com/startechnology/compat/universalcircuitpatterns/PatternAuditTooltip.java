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
        Level level = event.getEntity() == null ? null : event.getEntity().level();
        if (level == null) {
            return;
        }

        try {
            var result = PatternAudit.inspect(event.getItemStack(), level);
            if (result == null || !result.hasCircuits()) {
                return;
            }

            if (result.passes()) {
                event.getToolTip().add(Component.literal("Circuit audit: Universal Circuit OK")
                        .withStyle(ChatFormatting.GREEN));
            } else {
                event.getToolTip().add(Component.literal("Circuit audit: RE-ENCODE - conventional circuit input")
                        .withStyle(ChatFormatting.RED));
                result.conventionalCircuits().forEach(key -> event.getToolTip().add(
                        Component.literal("  ").append(key.getDisplayName()).withStyle(ChatFormatting.DARK_RED)));
            }
        } catch (RuntimeException ignored) {
            // A malformed or foreign encoded pattern should retain its normal AE2 tooltip.
        }
    }
}
