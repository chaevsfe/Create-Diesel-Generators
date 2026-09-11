package com.jesz.createdieselgenerators.compat.rei.category;

import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.compat.rei.CDGReiCategories;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.client.category.CreateReiCategory;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiLayout;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class DistillationCategory extends CreateReiCategory<CreateReiDisplay> {
    private static final int TOWER_X = 93;
    private static final int TOWER_BASE_Y = 140;
    private static final int TOWER_STEP = 20;
    private static final int TOWER_TOP_MARGIN = 24;
    private static final int OUTPUT_BASE_Y = 127;
    private static final int OUTPUT_STEP = 23;
    private static final int CONTENT_BOTTOM = 190;
    private static final int MAX_TIERS = 4;
    private static final int MARGIN = 4;

    public DistillationCategory() {
        super(CDGReiCategories.titleKey("distillation"));
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CDGReiCategories.DISTILLATION;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.FLUID_TANK, CDGItems.DISTILLATION_CONTROLLER);
    }

    @Override
    protected int contentHeight() {
        return CONTENT_BOTTOM - contentTop(MAX_TIERS) + MARGIN * 2;
    }

    private static int contentTop(int outputs) {
        int topSlot = OUTPUT_BASE_Y - OUTPUT_STEP * Math.max(outputs - 1, 0);
        int topTier = TOWER_BASE_Y - TOWER_STEP * outputs - TOWER_TOP_MARGIN;
        return Math.min(topSlot, topTier);
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        HeatCondition heat = CreateReiLayout.heatOf(display);
        List<EntryIngredient> outputs = display.outputs();
        int dy = contentTop(outputs.size()) - MARGIN;

        panel.texture(AllGuiTextures.JEI_ARROW, 40, 150 - dy);
        CreateReiLayout.shadow(panel, heat, 81, 163 - dy, 183 - dy);
        for (int i = 0; i <= outputs.size(); i++) {
            panel.blockPip(TOWER_X, TOWER_BASE_Y - TOWER_STEP * i - dy, AllBlocks.FLUID_TANK.defaultBlockState());
        }
        CreateReiLayout.blazeBurner(panel, heat, 91, 164 - dy);
        CreateReiLayout.heatBar(panel, heat, 4, 170 - dy);

        if (!display.inputs().isEmpty()) {
            panel.slot(17, 145 - dy, display.inputs().get(0));
        }
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(130, OUTPUT_BASE_Y - OUTPUT_STEP * i - dy, outputs.get(i), display.chance(i));
        }
        CreateReiLayout.heatSlots(panel, display, 134, 171 - dy);
    }
}
