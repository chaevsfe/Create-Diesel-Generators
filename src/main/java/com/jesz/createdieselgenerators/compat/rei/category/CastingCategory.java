package com.jesz.createdieselgenerators.compat.rei.category;

import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.compat.rei.CDGReiCategories;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.SpoutRenderState;
import dev.architectury.fluid.FluidStack;
import dev.chaevsfe.createreiviewer.client.category.CreateReiCategory;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiLayout;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;

public class CastingCategory extends CreateReiCategory<CreateReiDisplay> {
    public CastingCategory() {
        super(CDGReiCategories.titleKey("casting"));
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CDGReiCategories.CASTING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.SPOUT, CDGItems.MOLD);
    }

    @Override
    protected int contentHeight() {
        return 70;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_SHADOW, 62, 57);
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 126, 29);

        EntryIngredient fluidInput = display.inputs().isEmpty() ? EntryIngredient.empty() : display.inputs().get(0);
        FluidStack fluid = firstFluid(fluidInput);
        if (fluid != null) {
            panel.pip(75, 1, (pose, x, y) -> new SpoutRenderState(0, pose, fluid.getFluid(), fluid.getPatch(), x, y, 0));
        }
        panel.slot(27, 13, CreateReiLayout.catalyst(display, 0));
        panel.slot(27, 51, fluidInput);
        CreateReiLayout.outputGrid(panel, display, 142, 51);
    }

    private static FluidStack firstFluid(EntryIngredient entries) {
        for (EntryStack<?> stack : entries) {
            if (stack.getValue() instanceof FluidStack fluid) {
                return fluid;
            }
        }
        return null;
    }
}
