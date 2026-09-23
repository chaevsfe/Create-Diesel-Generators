package com.jesz.createdieselgenerators.client.gui.render;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;

@Environment(EnvType.CLIENT)
public class DistillationTowerRenderer extends CDGGuiBlockRenderer<DistillationTowerRenderState> {
    public DistillationTowerRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    protected void render(DistillationTowerRenderState state, PoseStack matrices) {
        matrices.scale(1, 1, -1);
        matrices.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrices.translate(-0.5f, -0.2f, -0.5f);
        matrices.scale(1, -1, 1);

        int tiers = state.tiers();
        for (int i = 0; i < tiers; i++) {
            PartialModel part = i == 0 ? CDGPartialModels.JEI_DISTILLER_BOTTOM
                : i == tiers - 1 ? CDGPartialModels.JEI_DISTILLER_TOP : CDGPartialModels.JEI_DISTILLER_MIDDLE;
            matrices.pushPose();
            matrices.translate(0, i, 0);
            matrices.rotateAround(Axis.YP.rotationDegrees(90), 0.5f, 0.5f, 0.5f);
            partial(part, Blocks.AIR.defaultBlockState());
            matrices.popPose();
        }

        for (Direction direction : Iterate.horizontalDirections) {
            matrices.pushPose();
            matrices.rotateAround(Axis.YP.rotationDegrees(direction.toYRot()), 0.5f, 0.5f, 0.5f);
            matrices.translate(2 / 16f, 0, 0);
            partial(CDGPartialModels.DISTILLATION_GAUGE, Blocks.AIR.defaultBlockState());
            matrices.popPose();
        }
    }

    @Override
    protected String getTextureLabel() {
        return "Distillation Tower";
    }

    @Override
    public Class<DistillationTowerRenderState> getRenderStateClass() {
        return DistillationTowerRenderState.class;
    }
}
