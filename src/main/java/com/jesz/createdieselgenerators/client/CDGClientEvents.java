package com.jesz.createdieselgenerators.client;

import com.jesz.createdieselgenerators.client.render.AndesiteGirderWrenchPreview;
import com.jesz.createdieselgenerators.client.sound.CDGSounds;
import com.jesz.createdieselgenerators.content.track_layers_bag.TrackLayersBagPlacement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public final class CDGClientEvents {
    private CDGClientEvents() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                AndesiteGirderWrenchPreview.tick();
                TrackLayersBagPlacement.clientTick();
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> CDGSounds.reset());
    }
}
