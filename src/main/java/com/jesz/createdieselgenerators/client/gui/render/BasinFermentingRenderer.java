package com.jesz.createdieselgenerators.client.gui.render;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.content.basin_lid.BasinLidBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.foundation.gui.render.GuiBlockRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class BasinFermentingRenderer extends GuiBlockRenderer<BasinFermentingRenderState> {
    @Override
    protected void renderToTexture(BasinFermentingRenderState state, PoseStack matrices, SubmitNodeCollector queue) {
        matrices.scale(1, 1, -1);
        matrices.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrices.translate(-0.5f, -1.8f, -0.5f);
        matrices.scale(1, -1, 1);

        BlockState lid = CDGBlocks.BASIN_LID.getDefaultState().setValue(BasinLidBlock.ON_A_BASIN, true);
        float progress = AnimationTickHolder.getRenderTime() % 90 / 90;

        matrices.translate(0, -0.65f, 0);
        CachedBuffers.block(lid).submit(matrices, queue);
        CachedBuffers.partial(CDGPartialModels.SMALL_GAUGE_DIAL, lid)
            .center()
            .rotateYDegrees(-lid.getValue(BasinLidBlock.FACING).toYRot() + 180)
            .translate(0.5625f, -0.375f, 1.0625f)
            .uncenter()
            .rotateZDegrees(progress * -90 + 90)
            .submit(matrices, queue);

        matrices.translate(0, -1, 0);
        CachedBuffers.block(AllBlocks.BASIN.defaultBlockState()).submit(matrices, queue);
    }

    @Override
    protected String getTextureLabel() {
        return "Basin Fermenting";
    }

    @Override
    public Class<BasinFermentingRenderState> getRenderStateClass() {
        return BasinFermentingRenderState.class;
    }
}
