package com.jesz.createdieselgenerators.compat.rei;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.compat.rei.category.BasinFermentingCategory;
import com.jesz.createdieselgenerators.compat.rei.category.BulkFermentingCategory;
import com.jesz.createdieselgenerators.compat.rei.category.CastingCategory;
import com.jesz.createdieselgenerators.compat.rei.category.CompressionMoldingCategory;
import com.jesz.createdieselgenerators.compat.rei.category.DistillationCategory;
import com.jesz.createdieselgenerators.compat.rei.category.HammeringCategory;
import com.jesz.createdieselgenerators.compat.rei.category.WireCuttingCategory;
import com.zurrtum.create.AllItems;
import dev.chaevsfe.createreiviewer.api.CreateReiClientReport;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public final class CDGReiClientCategories {
    private CDGReiClientCategories() {
    }

    public static void register(CategoryRegistry registry) {
        registry.add(
            new BasinFermentingCategory(),
            new BulkFermentingCategory(),
            new CompressionMoldingCategory(),
            new CastingCategory(),
            new DistillationCategory(),
            new HammeringCategory(),
            new WireCuttingCategory()
        );

        registry.addWorkstations(CDGReiCategories.BASIN_FERMENTING,
            EntryStacks.of(CDGBlocks.BASIN_LID.asStack()), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CDGReiCategories.BULK_FERMENTING,
            EntryStacks.of(CDGBlocks.BULK_FERMENTER.asStack()));
        registry.addWorkstations(CDGReiCategories.COMPRESSION_MOLDING,
            EntryStacks.of(AllItems.MECHANICAL_PRESS), EntryStacks.of(CDGItems.MOLD.asStack()), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CDGReiCategories.CASTING,
            EntryStacks.of(AllItems.SPOUT), EntryStacks.of(CDGItems.MOLD.asStack()), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CDGReiCategories.DISTILLATION,
            EntryStacks.of(AllItems.FLUID_TANK), EntryStacks.of(CDGItems.DISTILLATION_CONTROLLER.asStack()));
        registry.addWorkstations(CDGReiCategories.HAMMERING, EntryStacks.of(CDGItems.HAMMER.asStack()));
        registry.addWorkstations(CDGReiCategories.WIRE_CUTTING, EntryStacks.of(CDGItems.WIRE_CUTTERS.asStack()));

        CreateReiClientReport.register("Diesel Generators", CDGReiCategories.ALL);
        CreateDieselGenerators.LOGGER.info("Registered {} Diesel Generators REI categories", CDGReiCategories.ALL.size());
    }
}
