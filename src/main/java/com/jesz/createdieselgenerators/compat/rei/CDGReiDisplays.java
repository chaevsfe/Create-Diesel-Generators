package com.jesz.createdieselgenerators.compat.rei;

import com.jesz.createdieselgenerators.CDGDataComponents;
import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.basin_lid.BasinFermentingRecipe;
import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermentingRecipe;
import com.jesz.createdieselgenerators.content.distillation.DistillationRecipe;
import com.jesz.createdieselgenerators.content.molds.CastingRecipe;
import com.jesz.createdieselgenerators.content.molds.CompressionMoldingRecipe;
import com.jesz.createdieselgenerators.content.tools.hammer.HammerRecipe;
import com.jesz.createdieselgenerators.content.tools.wire_cutters.WireCuttingRecipe;
import com.jesz.createdieselgenerators.recipe.CDGProcessingRecipe;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import dev.chaevsfe.createreiviewer.api.CreateReiApi;
import dev.chaevsfe.createreiviewer.api.CreateReiDisplayBuilder;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public final class CDGReiDisplays {
    private CDGReiDisplays() {
    }

    public static void register(ServerDisplayRegistry registry) {
        CreateReiApi.fill(registry, BasinFermentingRecipe.class, CDGRecipes.BASIN_FERMENTING.getType(),
            holder -> machine(CDGReiCategories.BASIN_FERMENTING, holder, holder.value()));
        CreateReiApi.fill(registry, BulkFermentingRecipe.class, CDGRecipes.BULK_FERMENTING.getType(),
            holder -> machine(CDGReiCategories.BULK_FERMENTING, holder, holder.value()));
        CreateReiApi.fill(registry, DistillationRecipe.class, CDGRecipes.DISTILLATION.getType(),
            holder -> machine(CDGReiCategories.DISTILLATION, holder, holder.value()));
        CreateReiApi.fill(registry, CompressionMoldingRecipe.class, CDGRecipes.COMPRESSION_MOLDING.getType(),
            holder -> molded(CDGReiCategories.COMPRESSION_MOLDING, holder, holder.value(), holder.value().mold()));
        CreateReiApi.fill(registry, CastingRecipe.class, CDGRecipes.CASTING.getType(),
            holder -> molded(CDGReiCategories.CASTING, holder, holder.value(), holder.value().mold()));
        CreateReiApi.fill(registry, HammerRecipe.class, CDGRecipes.HAMMERING.getType(),
            holder -> tool(CDGReiCategories.HAMMERING, holder, holder.value().ingredient(),
                CDGItems.HAMMER.asStack(), holder.value().results()));
        CreateReiApi.fill(registry, WireCuttingRecipe.class, CDGRecipes.WIRE_CUTTING.getType(),
            holder -> tool(CDGReiCategories.WIRE_CUTTING, holder, holder.value().ingredient(),
                CDGItems.WIRE_CUTTERS.asStack(), holder.value().results()));
        CreateDieselGenerators.LOGGER.info("Recipe fillers registered for {} Diesel Generators categories", CDGReiCategories.ALL.size());
    }

    public static void report(PluginManager<REICommonPlugin> manager, ReloadStage stage) {
        CreateReiApi.report(manager, stage, "Diesel Generators", CDGReiCategories.ALL, CreateDieselGenerators.LOGGER);
    }

    private static CreateReiDisplay machine(
        CategoryIdentifier<CreateReiDisplay> category,
        RecipeHolder<? extends Recipe<?>> holder,
        CDGProcessingRecipe<?> recipe
    ) {
        return contents(category, holder, recipe).heat(recipe.getRequiredHeat()).build();
    }

    private static CreateReiDisplay molded(
        CategoryIdentifier<CreateReiDisplay> category,
        RecipeHolder<? extends Recipe<?>> holder,
        CDGProcessingRecipe<?> recipe,
        Identifier moldType
    ) {
        return contents(category, holder, recipe)
            .catalyst(mold(moldType))
            .heat(recipe.getRequiredHeat())
            .build();
    }

    private static CreateReiDisplayBuilder contents(
        CategoryIdentifier<CreateReiDisplay> category,
        RecipeHolder<? extends Recipe<?>> holder,
        CDGProcessingRecipe<?> recipe
    ) {
        return CreateReiDisplayBuilder.of(category)
            .sizedInputs(recipe.getIngredients())
            .fluidInputs(recipe.getFluidIngredients())
            .results(recipe.getRollableResults())
            .fluidResults(recipe.getFluidResults())
            .duration(recipe.getProcessingDuration())
            .location(holder);
    }

    private static CreateReiDisplay tool(
        CategoryIdentifier<CreateReiDisplay> category,
        RecipeHolder<? extends Recipe<?>> holder,
        Ingredient ingredient,
        ItemStack toolStack,
        List<ProcessingOutput> results
    ) {
        return CreateReiDisplayBuilder.of(category)
            .input(ingredient)
            .catalyst(EntryIngredient.of(EntryStacks.of(toolStack)))
            .results(results)
            .location(holder)
            .build();
    }

    private static EntryIngredient mold(Identifier moldType) {
        ItemStack stack = CDGItems.MOLD.asStack();
        stack.set(CDGDataComponents.MOLD_TYPE, moldType);
        return EntryIngredient.of(EntryStacks.of(stack));
    }
}
