package com.startechnology.compat.universalcircuitpatterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;

import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.menu.implementations.PatternAccessTermMenu;
import com.startechnology.compat.universalcircuitpatterns.mixin.ExtendedPatternAccessTermScreenAccessor;
import com.startechnology.compat.universalcircuitpatterns.mixin.ExtendedPatternProviderInfoAccessor;
import appeng.api.config.ShowPatternProviders;
import com.startechnology.compat.universalcircuitpatterns.mixin.PatternAccessTermScreenAccessor;

@Mod.EventBusSubscriber(modid = UniversalCircuitPatterns.MOD_ID, value = Dist.CLIENT)
public final class PatternAccessTerminalAudit {
    private PatternAccessTerminalAudit() {}

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!PresetConfig.ENABLE_AUDIT.get()) {
            return;
        }
        var screen = event.getScreen();
        boolean supportedScreen = screen instanceof PatternAccessTermScreen<?>
                || screen instanceof ExtendedPatternAccessTermScreenAccessor;
        if (!supportedScreen || event.getKeyCode() != GLFW.GLFW_KEY_A
                || (event.getModifiers() & GLFW.GLFW_MOD_CONTROL) == 0
                || (event.getModifiers() & GLFW.GLFW_MOD_SHIFT) == 0) {
            return;
        }

        runAudit(screen);
        event.setCanceled(true);
    }

    private static void runAudit(net.minecraft.client.gui.screens.Screen screen) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)
                || !(containerScreen.getMenu() instanceof PatternAccessTermMenu menu)) {
            return;
        }
        if (menu.getShownProviders() != ShowPatternProviders.ALL) {
            minecraft.player.sendSystemMessage(Component.literal(
                    "Set the Pattern Access Terminal provider filter to Show All, then run Ctrl+Shift+A again.")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        int processingPatterns = 0;
        int matchingPatterns = 0;
        int passingPatterns = 0;
        List<Component> failures = new ArrayList<>();

        Map<Long, PatternContainerRecord> records;
        Map<Long, ExtendedPatternProviderInfoAccessor> providerInfo = new HashMap<>();
        if (screen instanceof PatternAccessTermScreenAccessor accessor) {
            records = accessor.start$getPatternContainers();
        } else if (screen instanceof ExtendedPatternAccessTermScreenAccessor accessor) {
            records = accessor.start$getExtendedPatternContainers();
            providerInfo = accessor.start$getExtendedPatternProviderInfo();
        } else {
            return;
        }
        for (var recordEntry : records.entrySet()) {
            PatternContainerRecord record = recordEntry.getValue();
            var inventory = record.getInventory();
            for (int slot = 0; slot < inventory.size(); slot++) {
                try {
                    var result = PatternAudit.inspect(inventory.getStackInSlot(slot), minecraft.level);
                    if (result == null) {
                        continue;
                    }
                    processingPatterns++;
                    if (!result.hasPreferences()) {
                        continue;
                    }
                    matchingPatterns++;
                    if (result.passes()) {
                        passingPatterns++;
                    } else {
                        Component line = Component.literal(" - ")
                                .append(result.outputName().copy().withStyle(ChatFormatting.RED))
                                .append(Component.literal(" in ").withStyle(ChatFormatting.GRAY))
                                .append(record.getGroup().name().copy().withStyle(ChatFormatting.YELLOW));
                        var location = providerInfo.get(recordEntry.getKey());
                        if (location != null) {
                            var pos = location.start$getBlockPos();
                            String coordinates = pos.getX() + " " + pos.getY() + " " + pos.getZ();
                            String dimension = location.start$getWorld().location().toString();
                            line = line.copy().append(Component.literal(" @ " + coordinates + " [" + dimension + "]")
                                    .withStyle(style -> style.withColor(ChatFormatting.AQUA)
                                            .withClickEvent(new ClickEvent(
                                                    ClickEvent.Action.SUGGEST_COMMAND, "/tp " + coordinates))));
                        }
                        failures.add(line);
                        result.mismatches().forEach(mismatch -> failures.add(
                                Component.literal("   ").append(mismatch.description()).withStyle(ChatFormatting.GOLD)));
                    }
                } catch (RuntimeException ignored) {
                    // Keep auditing if a malformed or foreign pattern cannot be decoded.
                }
            }
        }

        int failedPatterns = matchingPatterns - passingPatterns;
        minecraft.player.sendSystemMessage(Component.literal("Tag preference pattern audit")
                .withStyle(ChatFormatting.AQUA));
        minecraft.player.sendSystemMessage(Component.literal(
                "Checked " + processingPatterns + " processing patterns; " + matchingPatterns
                        + " match configured tags. " + passingPatterns + " match preferences, " + failedPatterns + " need review.")
                .withStyle(failedPatterns == 0 ? ChatFormatting.GREEN : ChatFormatting.GOLD));
        if (failedPatterns > 0) minecraft.player.sendSystemMessage(Component.literal(
                "Differences are advisory; verify recipe alternatives before re-encoding.").withStyle(ChatFormatting.GRAY));
        failures.forEach(minecraft.player::sendSystemMessage);
    }
}
