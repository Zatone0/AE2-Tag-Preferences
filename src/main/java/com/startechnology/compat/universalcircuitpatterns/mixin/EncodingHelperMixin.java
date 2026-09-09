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
            method = {"encodeBestMatchingStacksIntoSlots", "encodeCraftingRecipe"},
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
    // AE2's known crafting-recipe path chooses from network stock directly,
    // without calling findBestIngredient. Override only the chosen grid value.
    @Redirect(
            method = "encodeCraftingRecipe",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    remap = true),
            require = 3)
    private static Object start$preferCraftingIngredient(
            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> grid,
            int index, Object value,
            appeng.menu.me.items.PatternEncodingTermMenu menu,
            net.minecraft.world.item.crafting.Recipe<?> recipe,
            List<List<GenericStack>> ingredients,
            java.util.function.Predicate<net.minecraft.world.item.ItemStack> filter) {
        var chosen = (net.minecraft.world.item.ItemStack) value;
        if (recipe != null) {
            var recipeIngredients = appeng.util.CraftingRecipeUtil.ensure3by3CraftingMatrix(recipe);
            if (index < recipeIngredients.size()) {
                var preferred = PreferredIngredientPresets.findPreferredCrafting(recipeIngredients.get(index), filter);
                if (preferred != null) {
                    preferred.setCount(chosen.getCount());
                    chosen = preferred;
                }
            }
        }
        return grid.set(index, chosen);
    }
}
