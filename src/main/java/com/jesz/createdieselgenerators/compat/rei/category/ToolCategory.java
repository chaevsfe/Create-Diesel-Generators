package com.jesz.createdieselgenerators.compat.rei.category;

import com.jesz.createdieselgenerators.compat.rei.CDGReiCategories;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import dev.chaevsfe.createreiviewer.client.category.CreateReiCategory;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiLayout;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.world.level.ItemLike;

public abstract class ToolCategory extends CreateReiCategory<CreateReiDisplay> {
    private final CategoryIdentifier<CreateReiDisplay> identifier;
    private final ItemLike tool;

    protected ToolCategory(CategoryIdentifier<CreateReiDisplay> identifier, String path, ItemLike tool) {
        super(CDGReiCategories.titleKey(path));
        this.identifier = identifier;
        this.tool = tool;
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return identifier;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(tool);
    }

    @Override
    protected int contentHeight() {
        return 55;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_SHADOW, 61, 21);
        panel.texture(AllGuiTextures.JEI_LONG_ARROW, 52, 32);
        panel.slot(81, 5, CreateReiLayout.catalyst(display, 0));
        panel.slot(27, 29, display.inputs().get(0));
        CreateReiLayout.outputGrid(panel, display, 142, 29);
    }
}
