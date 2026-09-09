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
        return 200;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        HeatCondition heat = CreateReiLayout.heatOf(display);
        List<EntryIngredient> outputs = display.outputs();

        panel.texture(AllGuiTextures.JEI_ARROW, 40, 150);
        CreateReiLayout.shadow(panel, heat, 81, 163, 183);
        for (int i = 0; i <= outputs.size(); i++) {
            panel.blockPip(TOWER_X, TOWER_BASE_Y - TOWER_STEP * i, AllBlocks.FLUID_TANK.defaultBlockState());
        }
        CreateReiLayout.blazeBurner(panel, heat, 91, 164);
        CreateReiLayout.heatBar(panel, heat, 4, 170);

        if (!display.inputs().isEmpty()) {
            panel.slot(17, 145, display.inputs().get(0));
        }
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(130, 127 - 23 * i, outputs.get(i), display.chance(i));
        }
        CreateReiLayout.heatSlots(panel, display, 134, 171);
    }
}
