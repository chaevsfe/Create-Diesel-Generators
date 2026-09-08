package com.jesz.createdieselgenerators.content.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class ChemicalSprayerProjectileRenderer extends EntityRenderer<ChemicalSprayerProjectileEntity, EntityRenderState> {
    public ChemicalSprayerProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
    }
}
