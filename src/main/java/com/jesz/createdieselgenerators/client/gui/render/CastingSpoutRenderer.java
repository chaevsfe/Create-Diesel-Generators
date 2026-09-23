package com.jesz.createdieselgenerators.client.gui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.FluidRenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

@Environment(EnvType.CLIENT)
public class CastingSpoutRenderer extends CDGGuiBlockRenderer<CastingSpoutRenderState> {
    public CastingSpoutRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    protected void render(CastingSpoutRenderState state, PoseStack matrices) {
        matrices.scale(1, 1, -1);
        matrices.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrices.translate(-0.5f, -1.8f, -0.5f);
        matrices.scale(1, -1, 1);

        float cycle = AnimationTickHolder.getRenderTime() % 30;
        float squeeze = cycle < 20 ? Mth.sin(cycle / 20f * Mth.PI) : 0;
        float move = 3 * squeeze / 32f;

        block(AllBlocks.SPOUT.defaultBlockState());
        matrices.pushPose();
        matrices.translate(0, move, 0);
        partial(AllPartialModels.SPOUT_MIDDLE, Blocks.AIR.defaultBlockState());
        matrices.translate(0, move, 0);
        partial(AllPartialModels.SPOUT_BOTTOM, Blocks.AIR.defaultBlockState());
        matrices.popPose();

        if (state.fluid() != Fluids.EMPTY) {
            FluidStateModelSet models = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
            float from = 0.15f;
            float to = 0.85f;
            FluidRenderHelper.extractFluidRenderState(null, null, models, state.fluid(), state.components(),
                from, from, from, to, to, to, LightCoordsUtil.FULL_BRIGHT, false, true).render(matrices, bufferSource);
            float half = 0.05f * squeeze;
            if (half > 0) {
                FluidRenderHelper.extractFluidRenderState(null, null, models, state.fluid(), state.components(),
                    0.5f - half, -1.5f, 0.5f - half, 0.5f + half, 2 * move - 0.4f, 0.5f + half, LightCoordsUtil.FULL_BRIGHT, false, true)
                    .render(matrices, bufferSource);
            }
        }

        matrices.translate(0, -1.65f, 0);
        block(AllBlocks.BASIN.defaultBlockState());
    }

    @Override
    protected String getTextureLabel() {
        return "Casting Spout";
    }

    @Override
    public Class<CastingSpoutRenderState> getRenderStateClass() {
        return CastingSpoutRenderState.class;
    }
}
