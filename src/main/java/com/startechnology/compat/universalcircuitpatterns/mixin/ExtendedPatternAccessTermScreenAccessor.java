package com.startechnology.compat.universalcircuitpatterns.mixin;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.client.gui.me.patternaccess.PatternContainerRecord;

@Pseudo
@Mixin(targets = "com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal", remap = false)
public interface ExtendedPatternAccessTermScreenAccessor {
    @Accessor("byId")
    HashMap<Long, PatternContainerRecord> start$getExtendedPatternContainers();

    @Accessor("infoMap")
    HashMap<Long, ExtendedPatternProviderInfoAccessor> start$getExtendedPatternProviderInfo();
}
