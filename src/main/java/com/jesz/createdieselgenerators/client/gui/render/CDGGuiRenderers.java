package com.jesz.createdieselgenerators.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;

@Environment(EnvType.CLIENT)
public final class CDGGuiRenderers {
    private CDGGuiRenderers() {
    }

    public static void register() {
        PictureInPictureRendererRegistry.register(context -> new CastingSpoutRenderer());
    }
}
