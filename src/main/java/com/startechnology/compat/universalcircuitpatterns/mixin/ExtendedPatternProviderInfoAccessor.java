package com.startechnology.compat.universalcircuitpatterns.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal$PatternProviderInfo", remap = false)
public interface ExtendedPatternProviderInfoAccessor {
    @Accessor("pos")
    BlockPos start$getBlockPos();

    @Accessor("world")
    ResourceKey<Level> start$getWorld();
}
