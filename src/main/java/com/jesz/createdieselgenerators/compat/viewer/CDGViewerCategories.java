package com.jesz.createdieselgenerators.compat.viewer;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import net.minecraft.resources.Identifier;

public final class CDGViewerCategories {
    public static final Identifier BASIN_FERMENTING = CreateDieselGenerators.rl("basin_fermenting");
    public static final Identifier BULK_FERMENTING = CreateDieselGenerators.rl("bulk_fermenting");
    public static final Identifier COMPRESSION_MOLDING = CreateDieselGenerators.rl("compression_molding");
    public static final Identifier CASTING = CreateDieselGenerators.rl("casting");
    public static final Identifier DISTILLATION = CreateDieselGenerators.rl("distillation");
    public static final Identifier HAMMERING = CreateDieselGenerators.rl("hammering");
    public static final Identifier WIRE_CUTTING = CreateDieselGenerators.rl("wire_cutting");

    private CDGViewerCategories() {
    }

    public static String titleKey(Identifier category) {
        return category.getNamespace() + ".recipe." + category.getPath();
    }
}
