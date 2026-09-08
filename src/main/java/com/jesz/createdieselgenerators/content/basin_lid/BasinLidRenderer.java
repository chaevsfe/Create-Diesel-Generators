package com.jesz.createdieselgenerators.content.basin_lid;

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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.jesz.createdieselgenerators.content.basin_lid.BasinLidBlock.ON_A_BASIN;
import static com.zurrtum.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class BasinLidRenderer implements BlockEntityRenderer<BasinLidBlockEntity, BasinLidRenderer.BasinLidRenderState> {
    public BasinLidRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BasinLidRenderState createRenderState() {
        return new BasinLidRenderState();
    }

    @Override
    public void extractRenderState(BasinLidBlockEntity be, BasinLidRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.dial = null;
        BlockState blockState = state.blockState;
        if (!blockState.getValue(ON_A_BASIN))
            return;
        Direction facing = blockState.getValue(HORIZONTAL_FACING);
        state.dial = CachedBuffers.partial(CDGPartialModels.SMALL_GAUGE_DIAL, blockState)
                .center()
                .rotateYDegrees(-facing.toYRot() + 180)
                .translate(0.5625f, -0.375, 1.0625)
                .uncenter()
                .rotateZDegrees(be.progress * -90 + 90)
                .light(state.lightCoords)
                .extractRenderState();
    }

    @Override
    public void submit(BasinLidRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.dial != null)
            state.dial.submit(matrices, queue);
    }

    public static class BasinLidRenderState extends BlockEntityRenderState {
        public @Nullable SuperByteBufferRenderState dial;
    }
}
