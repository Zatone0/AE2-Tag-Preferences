package com.startechnology.compat.universalcircuitpatterns.mixin;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;

@Mixin(value = PatternAccessTermScreen.class, remap = false)
public interface PatternAccessTermScreenAccessor {
    @Accessor("byId")
    HashMap<Long, PatternContainerRecord> start$getPatternContainers();
}
