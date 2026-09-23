package com.jesz.createdieselgenerators.client.gui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.FluidRenderHelper;
import com.zurrtum.create.client.foundation.gui.render.GuiBlockRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

@Environment(EnvType.CLIENT)
public class CastingSpoutRenderer extends GuiBlockRenderer<CastingSpoutRenderState> {
    @Override
    protected void renderToTexture(CastingSpoutRenderState state, PoseStack matrices, SubmitNodeCollector queue) {
        matrices.scale(1, 1, -1);
        matrices.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrices.translate(-0.5f, -1.8f, -0.5f);
        matrices.scale(1, -1, 1);

        float cycle = AnimationTickHolder.getRenderTime() % 30;
        float squeeze = cycle < 20 ? Mth.sin(cycle / 20f * Mth.PI) : 0;
        float move = 3 * squeeze / 32f;

        CachedBuffers.block(AllBlocks.SPOUT.defaultBlockState()).submit(matrices, queue);
        matrices.pushPose();
        matrices.translate(0, move, 0);
        CachedBuffers.partial(AllPartialModels.SPOUT_MIDDLE, Blocks.AIR.defaultBlockState()).submit(matrices, queue);
        matrices.translate(0, move, 0);
        CachedBuffers.partial(AllPartialModels.SPOUT_BOTTOM, Blocks.AIR.defaultBlockState()).submit(matrices, queue);
        matrices.popPose();

        if (state.fluid() != Fluids.EMPTY) {
            FluidStateModelSet models = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
            float from = 0.15f;
            float to = 0.85f;
            FluidRenderHelper.extractFluidRenderState(null, null, models, state.fluid(), state.components(),
                from, from, from, to, to, to, 0, false, true).submit(matrices, queue);
            float half = 0.05f * squeeze;
            if (half > 0) {
                FluidRenderHelper.extractFluidRenderState(null, null, models, state.fluid(), state.components(),
                    0.5f - half, -1.5f, 0.5f - half, 0.5f + half, 2 * move - 0.4f, 0.5f + half, 0, false, true).submit(matrices, queue);
            }
        }

        matrices.translate(0, -1.65f, 0);
        CachedBuffers.block(AllBlocks.BASIN.defaultBlockState()).submit(matrices, queue);
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
