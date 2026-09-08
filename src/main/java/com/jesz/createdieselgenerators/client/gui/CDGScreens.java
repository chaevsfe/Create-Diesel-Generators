package com.jesz.createdieselgenerators.client.gui;

import com.jesz.createdieselgenerators.CDGMenuTypes;
import com.jesz.createdieselgenerators.content.entity_filter.EntityFilterMenu;
import com.jesz.createdieselgenerators.content.entity_filter.EntityFilterScreen;
import com.zurrtum.create.client.AllMenuScreens;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class CDGScreens {
    private CDGScreens() {
    }

    public static void register() {
        AllMenuScreens.register(CDGMenuTypes.ENTITY_FILTER,
                (client, type, syncId, inventory, title, buf) ->
                        new EntityFilterScreen(new EntityFilterMenu(syncId, inventory, ItemStack.STREAM_CODEC.decode(buf)), inventory, title));
    }
}
