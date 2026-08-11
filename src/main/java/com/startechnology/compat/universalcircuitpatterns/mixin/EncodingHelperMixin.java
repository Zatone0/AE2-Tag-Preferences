package com.startechnology.compat.universalcircuitpatterns.mixin;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.jeirei.EncodingHelper;
import com.startechnology.compat.universalcircuitpatterns.PreferredIngredientPresets;

@Mixin(value = EncodingHelper.class, remap = false)
public abstract class EncodingHelperMixin {
    @Shadow
    private static GenericStack findBestIngredient(Map<AEKey, Integer> priorities, List<GenericStack> candidates) {
        throw new AssertionError();
    }

    @Redirect(
            method = "encodeBestMatchingStacksIntoSlots",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/integration/modules/jeirei/EncodingHelper;findBestIngredient(Ljava/util/Map;Ljava/util/List;)Lappeng/api/stacks/GenericStack;"))
    private static GenericStack start$preferConfiguredIngredient(
            Map<AEKey, Integer> priorities, List<GenericStack> candidates) {
        GenericStack preferred = PreferredIngredientPresets.findPreferred(candidates);
        if (preferred != null) {
            return preferred;
        }

        return findBestIngredient(priorities, candidates);
    }
}
