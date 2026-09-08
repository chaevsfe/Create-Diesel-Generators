package com.jesz.createdieselgenerators.content.burner;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BurnerRenderer extends ShaftRenderer {
    public BurnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BurnerRenderState createRenderState() {
        return new BurnerRenderState();
    }

    @Override
    public void extractRenderState(KineticBlockEntity be, SingleKineticRenderState renderState, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, renderState, partialTicks, cameraPos, crumblingOverlay);
        BurnerRenderState state = (BurnerRenderState) renderState;
        BurnerBlockEntity burner = (BurnerBlockEntity) be;
        BlockState blockState = state.blockState;
        float rotation = Mth.lerp(Mth.lerp(partialTicks, burner.prevValveState, burner.valveState), -45, 45);
        float yRot = blockState.getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0;
        state.leftDial = dial(blockState, yRot, 0.25, rotation, state.lightCoords);
        state.rightDial = dial(blockState, yRot, 0.75, -rotation, state.lightCoords);
    }

    private static SuperByteBufferRenderState dial(BlockState blockState, float yRot, double x, float rotation, int light) {
        return CachedBuffers.partial(CDGPartialModels.SMALL_GAUGE_DIAL, blockState)
                .center()
                .rotateYDegrees(yRot)
                .uncenter()
                .translate(x, 0.25, 0.5)
                .rotateXDegrees(rotation)
                .light(light)
                .extractRenderState();
    }

    @Override
    public void submit(SingleKineticRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        super.submit(renderState, matrices, queue, cameraState);
        BurnerRenderState state = (BurnerRenderState) renderState;
        if (state.leftDial != null)
            state.leftDial.submit(matrices, queue);
        if (state.rightDial != null)
            state.rightDial.submit(matrices, queue);
    }

    public static class BurnerRenderState extends SingleKineticRenderState {
        public @Nullable SuperByteBufferRenderState leftDial;
        public @Nullable SuperByteBufferRenderState rightDial;
    }
}
