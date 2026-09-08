package com.jesz.createdieselgenerators.client.gui;

import com.jesz.createdieselgenerators.content.track_layers_bag.TrackLayersBagComponent;
import com.jesz.createdieselgenerators.content.track_layers_bag.TrackLayersBagTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;

@Environment(EnvType.CLIENT)
public final class CDGTooltipComponents {
    private CDGTooltipComponents() {
    }

    public static void register() {
        ClientTooltipComponentCallback.EVENT.register(data ->
                data instanceof TrackLayersBagTooltip tooltip ? new TrackLayersBagComponent(tooltip.stack()) : null);
    }
}
