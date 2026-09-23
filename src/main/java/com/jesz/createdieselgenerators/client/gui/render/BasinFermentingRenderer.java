package com.jesz.createdieselgenerators.client.gui.render;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.content.basin_lid.BasinLidBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class BasinFermentingRenderer extends CDGGuiBlockRenderer<BasinFermentingRenderState> {
    public BasinFermentingRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    protected void render(BasinFermentingRenderState state, PoseStack matrices) {
        matrices.scale(1, 1, -1);
        matrices.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrices.translate(-0.5f, -1.8f, -0.5f);
        matrices.scale(1, -1, 1);

        BlockState lid = CDGBlocks.BASIN_LID.getDefaultState().setValue(BasinLidBlock.ON_A_BASIN, true);
        float progress = AnimationTickHolder.getRenderTime() % 90 / 90;

        matrices.translate(0, -0.65f, 0);
        block(lid);
        matrices.pushPose();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.mulPose(Axis.YP.rotationDegrees(-lid.getValue(BasinLidBlock.FACING).toYRot() + 180));
        matrices.translate(0.5625f, -0.375f, 1.0625f);
        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.mulPose(Axis.ZP.rotationDegrees(progress * -90 + 90));
        partial(CDGPartialModels.SMALL_GAUGE_DIAL, lid);
        matrices.popPose();

        matrices.translate(0, -1, 0);
        block(AllBlocks.BASIN.defaultBlockState());
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
