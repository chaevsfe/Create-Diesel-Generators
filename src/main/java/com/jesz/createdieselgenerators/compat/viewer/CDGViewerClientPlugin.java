package com.jesz.createdieselgenerators.compat.viewer;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.client.gui.render.CastingSpoutRenderState;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.PressBasinRenderState;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.ViewerRecipe;
import dev.chaevsfe.createreiviewer.api.ViewerStack;
import dev.chaevsfe.createreiviewer.api.client.CreateViewerClientPlugin;
import dev.chaevsfe.createreiviewer.api.client.ViewerCanvas;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategory;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategoryRegistry;
import dev.chaevsfe.createreiviewer.api.client.ViewerLayouts;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class CDGViewerClientPlugin implements CreateViewerClientPlugin {
    private static final int TOWER_X = 93;
    private static final int TOWER_BASE_Y = 140;
    private static final int TOWER_STEP = 20;
    private static final int TOWER_TOP_MARGIN = 6;
    private static final int OUTPUT_BASE_Y = 127;
    private static final int OUTPUT_STEP = 23;
    private static final int CONTENT_BOTTOM = 190;
    private static final int MAX_TIERS = 3;
    private static final int MARGIN = 4;

    @Override
    public void registerCategories(ViewerCategoryRegistry registry) {
        registry.add(category(CDGViewerCategories.BASIN_FERMENTING)
            .icon(AllItems.BASIN, CDGBlocks.BASIN_LID)
            .height(ViewerLayouts.BASIN_HEIGHT)
            .overhangTop(ViewerLayouts.BASIN_OVERHANG_TOP)
            .workstations(CDGBlocks.BASIN_LID, AllItems.BASIN)
            .layout(CDGViewerClientPlugin::basinFermenting)
            .build());
        registry.add(category(CDGViewerCategories.BULK_FERMENTING)
            .icon(CDGBlocks.BULK_FERMENTER)
            .height(103)
            .overhangTop(5)
            .workstations(CDGBlocks.BULK_FERMENTER)
            .layout(CDGViewerClientPlugin::bulkFermenting)
            .build());
        registry.add(category(CDGViewerCategories.COMPRESSION_MOLDING)
            .icon(AllItems.MECHANICAL_PRESS, CDGItems.MOLD)
            .height(ViewerLayouts.BASIN_HEIGHT)
            .overhangTop(ViewerLayouts.BASIN_OVERHANG_TOP)
            .workstations(AllItems.MECHANICAL_PRESS, CDGItems.MOLD, AllItems.BASIN)
            .layout(CDGViewerClientPlugin::compressionMolding)
            .build());
        registry.add(category(CDGViewerCategories.CASTING)
            .icon(AllItems.SPOUT, CDGItems.MOLD)
            .height(ViewerLayouts.BASIN_HEIGHT)
            .overhangTop(ViewerLayouts.BASIN_OVERHANG_TOP)
            .workstations(AllItems.SPOUT, CDGItems.MOLD, AllItems.BASIN)
            .layout(CDGViewerClientPlugin::casting)
            .build());
        registry.add(category(CDGViewerCategories.DISTILLATION)
            .icon(AllItems.FLUID_TANK, CDGItems.DISTILLATION_CONTROLLER)
            .height(CONTENT_BOTTOM - distillationTop(MAX_TIERS) + MARGIN * 2)
            .workstations(AllItems.FLUID_TANK, CDGItems.DISTILLATION_CONTROLLER)
            .layout(CDGViewerClientPlugin::distillation)
            .build());
        registry.add(category(CDGViewerCategories.HAMMERING)
            .icon(CDGItems.HAMMER)
            .height(55)
            .workstations(CDGItems.HAMMER)
            .layout(CDGViewerClientPlugin::tool)
            .build());
        registry.add(category(CDGViewerCategories.WIRE_CUTTING)
            .icon(CDGItems.WIRE_CUTTERS)
            .height(55)
            .workstations(CDGItems.WIRE_CUTTERS)
            .layout(CDGViewerClientPlugin::tool)
            .build());
    }

    private static ViewerCategory.Builder category(Identifier id) {
        return ViewerCategory.builder(id).title(CDGViewerCategories.titleKey(id));
    }

    private static void basinFermenting(ViewerRecipe recipe, ViewerCanvas canvas) {
        ViewerLayouts.basinBackground(canvas, recipe.heat(), recipe.outputs().size());
        canvas.blockPip(93, 42, CDGBlocks.BASIN_LID.getDefaultState());
        ViewerLayouts.basinInputs(canvas, recipe);
        ViewerLayouts.basinOutputs(canvas, recipe, 51);
        ViewerLayouts.basinHeatSlots(canvas, recipe);
    }

    private static void bulkFermenting(ViewerRecipe recipe, ViewerCanvas canvas) {
        HeatCondition heat = recipe.heat();
        canvas.texture(AllGuiTextures.JEI_DOWN_ARROW, 136, 20);
        ViewerLayouts.shadow(canvas, heat, 81, 68, 88);
        canvas.blockPip(93, 42, CDGBlocks.BULK_FERMENTER.getDefaultState());
        ViewerLayouts.blazeBurner(canvas, heat, 91, 69);
        ViewerLayouts.heatBar(canvas, heat, 4, 80);
        ViewerLayouts.inputGrid(canvas, recipe.inputs(), 2, 41);
        ViewerLayouts.outputGrid(canvas, recipe, 142, 51);
        ViewerLayouts.heatSlots(canvas, recipe, 134, 81);
    }

    private static void compressionMolding(ViewerRecipe recipe, ViewerCanvas canvas) {
        ViewerLayouts.basinBackground(canvas, recipe.heat(), recipe.outputs().size());
        canvas.pip(91, -5, PressBasinRenderState::new);
        ViewerLayouts.basinInputs(canvas, recipe);
        ViewerLayouts.basinOutputs(canvas, recipe, 51);
        canvas.slot(36, 11, recipe.catalyst(0));
        ViewerLayouts.heatSlots(canvas, recipe, 134, 81, 1);
    }

    private static void casting(ViewerRecipe recipe, ViewerCanvas canvas) {
        canvas.texture(AllGuiTextures.JEI_SHADOW, 81, 68);
        canvas.texture(AllGuiTextures.JEI_DOWN_ARROW, 136, 32);
        ViewerIngredient fluidInput = recipe.input(0);
        ViewerStack.OfFluid fluid = ViewerLayouts.firstFluid(fluidInput);
        if (fluid != null) {
            canvas.pip(91, -5, (pose, x, y) -> new CastingSpoutRenderState(pose, fluid.fluid(), fluid.components(), x, y));
        }
        canvas.slot(36, 11, recipe.catalyst(0));
        canvas.slot(36, 51, fluidInput);
        ViewerLayouts.basinOutputs(canvas, recipe, 51);
    }

    private static int distillationTop(int outputs) {
        int topSlot = OUTPUT_BASE_Y - OUTPUT_STEP * Math.max(outputs - 1, 0);
        int topTier = TOWER_BASE_Y - TOWER_STEP * outputs - TOWER_TOP_MARGIN;
        return Math.min(topSlot, topTier);
    }

    private static void distillation(ViewerRecipe recipe, ViewerCanvas canvas) {
        HeatCondition heat = recipe.heat();
        List<ViewerIngredient> outputs = recipe.outputs();
        int dy = distillationTop(outputs.size()) - MARGIN;

        canvas.texture(AllGuiTextures.JEI_ARROW, 40, 150 - dy);
        ViewerLayouts.shadow(canvas, heat, 81, 163 - dy, 183 - dy);
        for (int i = 0; i <= outputs.size(); i++) {
            canvas.blockPip(TOWER_X, TOWER_BASE_Y - TOWER_STEP * i - dy, AllBlocks.FLUID_TANK.defaultBlockState());
        }
        ViewerLayouts.blazeBurner(canvas, heat, 91, 164 - dy);
        ViewerLayouts.heatBar(canvas, heat, 4, 170 - dy);

        if (!recipe.inputs().isEmpty()) {
            canvas.slot(17, 145 - dy, recipe.input(0));
        }
        for (int i = 0; i < outputs.size(); i++) {
            canvas.output(130, OUTPUT_BASE_Y - OUTPUT_STEP * i - dy, outputs.get(i), recipe.chance(i));
        }
        ViewerLayouts.heatSlots(canvas, recipe, 134, 171 - dy);
    }

    private static void tool(ViewerRecipe recipe, ViewerCanvas canvas) {
        canvas.texture(AllGuiTextures.JEI_SHADOW, 61, 21);
        canvas.texture(AllGuiTextures.JEI_LONG_ARROW, 52, 32);
        canvas.slot(81, 5, recipe.catalyst(0));
        canvas.slot(27, 29, recipe.input(0));
        ViewerLayouts.outputGrid(canvas, recipe, 142, 29);
    }
}
