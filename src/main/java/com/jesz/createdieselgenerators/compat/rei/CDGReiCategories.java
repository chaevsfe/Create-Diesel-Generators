package com.jesz.createdieselgenerators.compat.rei;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import dev.chaevsfe.createreiviewer.api.CreateReiApi;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

import java.util.List;

public final class CDGReiCategories {
    public static final CategoryIdentifier<CreateReiDisplay> BASIN_FERMENTING = of("basin_fermenting");
    public static final CategoryIdentifier<CreateReiDisplay> BULK_FERMENTING = of("bulk_fermenting");
    public static final CategoryIdentifier<CreateReiDisplay> COMPRESSION_MOLDING = of("compression_molding");
    public static final CategoryIdentifier<CreateReiDisplay> CASTING = of("casting");
    public static final CategoryIdentifier<CreateReiDisplay> DISTILLATION = of("distillation");
    public static final CategoryIdentifier<CreateReiDisplay> HAMMERING = of("hammering");
    public static final CategoryIdentifier<CreateReiDisplay> WIRE_CUTTING = of("wire_cutting");

    public static final List<CategoryIdentifier<? extends CreateReiDisplay>> ALL = List.of(
        BASIN_FERMENTING,
        BULK_FERMENTING,
        COMPRESSION_MOLDING,
        CASTING,
        DISTILLATION,
        HAMMERING,
        WIRE_CUTTING
    );

    private CDGReiCategories() {
    }

    private static CategoryIdentifier<CreateReiDisplay> of(String path) {
        return CreateReiApi.category(CreateDieselGenerators.ID, path);
    }

    public static String titleKey(String path) {
        return CreateDieselGenerators.ID + ".recipe." + path;
    }
}
