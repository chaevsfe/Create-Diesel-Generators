package com.jesz.createdieselgenerators.compat.rei.category;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.compat.rei.CDGReiCategories;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.client.category.CreateReiCategory;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiLayout;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class BulkFermentingCategory extends CreateReiCategory<CreateReiDisplay> {
    public BulkFermentingCategory() {
        super(CDGReiCategories.titleKey("bulk_fermenting"));
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CDGReiCategories.BULK_FERMENTING;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(CDGBlocks.BULK_FERMENTER);
    }

    @Override
    protected int contentHeight() {
        return 103;
    }

    @Override
    protected int contentOverhangTop() {
        return 5;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        HeatCondition heat = CreateReiLayout.heatOf(display);
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 136, 20);
        CreateReiLayout.shadow(panel, heat, 81, 68, 88);
        panel.blockPip(93, 42, CDGBlocks.BULK_FERMENTER.getDefaultState());
        CreateReiLayout.blazeBurner(panel, heat, 91, 69);
        CreateReiLayout.heatBar(panel, heat, 4, 80);
        CreateReiLayout.inputGrid(panel, display.inputs(), 2, 41);
        CreateReiLayout.outputGrid(panel, display, 142, 51);
        CreateReiLayout.heatSlots(panel, display, 134, 81);
    }
}
