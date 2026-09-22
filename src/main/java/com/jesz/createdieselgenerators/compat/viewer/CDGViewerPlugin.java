package com.jesz.createdieselgenerators.compat.viewer;

import com.jesz.createdieselgenerators.CDGDataComponents;
import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.compat.CDGRecipeSync;
import com.jesz.createdieselgenerators.content.basin_lid.BasinFermentingRecipe;
import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermentingRecipe;
import com.jesz.createdieselgenerators.content.distillation.DistillationRecipe;
import com.jesz.createdieselgenerators.content.molds.CastingRecipe;
import com.jesz.createdieselgenerators.content.molds.CompressionMoldingRecipe;
import com.jesz.createdieselgenerators.content.tools.hammer.HammerRecipe;
import com.jesz.createdieselgenerators.content.tools.wire_cutters.WireCuttingRecipe;
import com.jesz.createdieselgenerators.recipe.CDGProcessingRecipe;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import dev.chaevsfe.createreiviewer.api.CreateViewerPlugin;
import dev.chaevsfe.createreiviewer.api.ViewerRecipe;
import dev.chaevsfe.createreiviewer.api.ViewerRecipeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public final class CDGViewerPlugin implements CreateViewerPlugin {
    @Override
    public void registerRecipes(ViewerRecipeRegistry registry) {
        registry.add(CDGViewerCategories.BASIN_FERMENTING, CDGRecipes.BASIN_FERMENTING.getType(), BasinFermentingRecipe.class,
            (holder, recipe) -> machine(recipe, holder.value()));
        registry.add(CDGViewerCategories.BULK_FERMENTING, CDGRecipes.BULK_FERMENTING.getType(), BulkFermentingRecipe.class,
            (holder, recipe) -> machine(recipe, holder.value()));
        registry.add(CDGViewerCategories.COMPRESSION_MOLDING, CDGRecipes.COMPRESSION_MOLDING.getType(), CompressionMoldingRecipe.class,
            (holder, recipe) -> molded(recipe, holder.value(), holder.value().mold()));
        registry.add(CDGViewerCategories.CASTING, CDGRecipes.CASTING.getType(), CastingRecipe.class,
            (holder, recipe) -> molded(recipe, holder.value(), holder.value().mold()));
        registry.add(CDGViewerCategories.DISTILLATION, CDGRecipes.DISTILLATION.getType(), DistillationRecipe.class,
            (holder, recipe) -> machine(recipe, holder.value()));
        registry.add(CDGViewerCategories.HAMMERING, CDGRecipes.HAMMERING.getType(), HammerRecipe.class,
            (holder, recipe) -> tool(recipe, holder.value().ingredient(), CDGItems.HAMMER.asStack(), holder.value().results()));
        registry.add(CDGViewerCategories.WIRE_CUTTING, CDGRecipes.WIRE_CUTTING.getType(), WireCuttingRecipe.class,
            (holder, recipe) -> tool(recipe, holder.value().ingredient(), CDGItems.WIRE_CUTTERS.asStack(), holder.value().results()));
        registry.synchronize(CDGRecipeSync.serializers().toArray(new RecipeSerializer<?>[0]));
    }

    private static ViewerRecipe machine(ViewerRecipe.Builder builder, CDGProcessingRecipe<?> recipe) {
        return contents(builder, recipe).heat(recipe.getRequiredHeat()).build();
    }

    private static ViewerRecipe molded(ViewerRecipe.Builder builder, CDGProcessingRecipe<?> recipe, Identifier moldType) {
        return contents(builder, recipe)
            .catalyst(mold(moldType))
            .heat(recipe.getRequiredHeat())
            .build();
    }

    private static ViewerRecipe.Builder contents(ViewerRecipe.Builder builder, CDGProcessingRecipe<?> recipe) {
        return builder
            .sizedInputs(recipe.getIngredients())
            .fluidInputs(recipe.getFluidIngredients())
            .results(recipe.getRollableResults())
            .fluidResults(recipe.getFluidResults())
            .duration(recipe.getProcessingDuration());
    }

    private static ViewerRecipe tool(ViewerRecipe.Builder builder, Ingredient ingredient, ItemStack toolStack, List<ProcessingOutput> results) {
        return builder
            .input(ingredient)
            .catalyst(toolStack)
            .results(results)
            .build();
    }

    private static ItemStack mold(Identifier moldType) {
        ItemStack stack = CDGItems.MOLD.asStack();
        stack.set(CDGDataComponents.MOLD_TYPE, moldType);
        return stack;
    }
}
