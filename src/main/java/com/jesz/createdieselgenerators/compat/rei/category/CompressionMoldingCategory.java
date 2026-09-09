package com.jesz.createdieselgenerators.compat.rei.category;

import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.compat.rei.CDGReiCategories;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.render.PressBasinRenderState;
import dev.chaevsfe.createreiviewer.client.category.BasinCategory;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiLayout;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class CompressionMoldingCategory extends BasinCategory {
    public CompressionMoldingCategory() {
        super(CDGReiCategories.titleKey("compression_molding"));
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CDGReiCategories.COMPRESSION_MOLDING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_PRESS, CDGItems.MOLD);
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        basinBackground(panel, heatOf(display), display.outputs().size());
        panel.pip(91, -5, PressBasinRenderState::new);
        basinInputs(panel, display);
        basinOutputs(panel, display, 51);
        panel.slot(36, 11, CreateReiLayout.catalyst(display, 0));
        CreateReiLayout.heatSlots(panel, display, 134, 81, 1);
    }
}
