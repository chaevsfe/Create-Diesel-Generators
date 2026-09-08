package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.content.entity_filter.EntityFilterMenu;
import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

public class CDGMenuTypes {

    public static final MenuType<ItemStack> ENTITY_FILTER = register("entity_filter", EntityFilterMenu::new);

    private static <T> MenuType<T> register(String name, MenuType<T> type) {
        return Registry.register(CreateRegistries.MENU_TYPE, CreateDieselGenerators.rl(name), type);
    }

    public static void register() {
    }
}
