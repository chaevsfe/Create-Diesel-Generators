package com.jesz.createdieselgenerators.compat.rei.category;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.compat.rei.CDGReiCategories;
import com.zurrtum.create.AllItems;
import dev.chaevsfe.createreiviewer.client.category.BasinCategory;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class BasinFermentingCategory extends BasinCategory {
    public BasinFermentingCategory() {
        super(CDGReiCategories.titleKey("basin_fermenting"));
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CDGReiCategories.BASIN_FERMENTING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.BASIN, CDGBlocks.BASIN_LID);
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        basinBackground(panel, heatOf(display), display.outputs().size());
        panel.blockPip(93, 42, CDGBlocks.BASIN_LID.getDefaultState());
        basinInputs(panel, display);
        basinOutputs(panel, display, 51);
        heatSlots(panel, display);
    }
}
