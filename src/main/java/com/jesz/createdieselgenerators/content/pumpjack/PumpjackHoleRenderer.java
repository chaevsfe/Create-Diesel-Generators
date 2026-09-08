package com.jesz.createdieselgenerators.content.pumpjack;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PumpjackHoleRenderer implements BlockEntityRenderer<PumpjackHoleBlockEntity, PumpjackHoleRenderer.PumpjackHoleRenderState> {
    public PumpjackHoleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public PumpjackHoleRenderState createRenderState() {
        return new PumpjackHoleRenderState();
    }

    @Override
    public void extractRenderState(PumpjackHoleBlockEntity be, PumpjackHoleRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.rope = CachedBuffers.partial(CDGPartialModels.PUMPJACK_ROPE, state.blockState)
                .translate(0.5, 0, 0.5)
                .scale(1, be.pipeLength, 1)
                .light(state.lightCoords)
                .extractRenderState();
    }

    @Override
    public void submit(PumpjackHoleRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.rope != null)
            state.rope.submit(matrices, queue);
    }

    public static class PumpjackHoleRenderState extends BlockEntityRenderState {
        public @Nullable SuperByteBufferRenderState rope;
    }
}
