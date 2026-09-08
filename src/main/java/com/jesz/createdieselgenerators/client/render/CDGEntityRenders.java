package com.jesz.createdieselgenerators.client.render;

import com.jesz.createdieselgenerators.CDGEntityTypes;
import com.jesz.createdieselgenerators.content.tools.ChemicalSprayerProjectileRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public final class CDGEntityRenders {
    private CDGEntityRenders() {
    }

    public static void register() {
        EntityRendererRegistry.register(CDGEntityTypes.CHEMICAL_SPRAYER_PROJECTILE.get(), ChemicalSprayerProjectileRenderer::new);
    }
}
