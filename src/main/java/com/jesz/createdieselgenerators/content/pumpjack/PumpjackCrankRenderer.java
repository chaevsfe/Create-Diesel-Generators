package com.jesz.createdieselgenerators.content.pumpjack;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.zurrtum.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class PumpjackCrankRenderer extends ShaftRenderer {
    public PumpjackCrankRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PumpjackCrankRenderState createRenderState() {
        return new PumpjackCrankRenderState();
    }

    @Override
    public void extractRenderState(KineticBlockEntity blockEntity, SingleKineticRenderState renderState, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(blockEntity, renderState, partialTicks, cameraPos, crumblingOverlay);
        PumpjackCrankRenderState state = (PumpjackCrankRenderState) renderState;
        PumpjackCrankBlockEntity be = (PumpjackCrankBlockEntity) blockEntity;
        state.crank = null;
        state.rod = null;
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState blockState = state.blockState;
        BlockPos pos = state.blockPos;
        float angle = AngleHelper.angleLerp(partialTicks, be.prevAngle, be.angle);

        boolean isXAxis = blockState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.X;
        double v = ((isXAxis ? angle : -angle) + 90) / 180 * Math.PI;

        boolean small = be.crankSize.getValue() == 0;
        double sin = Math.sin(v) * (small ? 0.8125 : 1.125);
        double cos = Math.cos(v) * (small ? 0.8125 : 1.125);
        SuperByteBuffer crank = CachedBuffers.partial(small ? CDGPartialModels.PUMPJACK_CRANK_SMALL : CDGPartialModels.PUMPJACK_CRANK_LARGE, blockState);
        SuperByteBuffer rod = CachedBuffers.partial(small ? CDGPartialModels.PUMPJACK_CRANK_ROD_SMALL : CDGPartialModels.PUMPJACK_CRANK_ROD_LARGE, blockState);

        double dstY = -1000 - sin - 1.25 - pos.getY();
        double dstX = pos.getX() - cos - 0.5 - pos.getX();
        double dstZ = pos.getZ() - cos - 0.5 - pos.getZ();

        if (be.bearingPos != null) {
            PumpjackBearingBlockEntity bearing = be.bearing.get();

            float interpolatedAngle = 0;
            if (bearing != null)
                interpolatedAngle = bearing.getInterpolatedAngle(partialTicks);
            if (be.inPonderAngle != Integer.MIN_VALUE)
                interpolatedAngle = be.inPonderAngle;

            if (!isXAxis)
                interpolatedAngle *= -1;
            Vec2 crankBearingLocation = new Vec2(
                    (float) (be.crankBearingLocation.x * Math.cos(interpolatedAngle / 180 * Math.PI) - be.crankBearingLocation.y * Math.sin(interpolatedAngle / 180 * Math.PI)) + 0.5f,
                    (float) (be.crankBearingLocation.x * Math.sin(interpolatedAngle / 180 * Math.PI) + be.crankBearingLocation.y * Math.cos(interpolatedAngle / 180 * Math.PI)) + 0.5f);
            if (isXAxis)
                crankBearingLocation = crankBearingLocation.add(new Vec2((float) be.bearingPos.getX(), (float) be.bearingPos.getY()));
            else
                crankBearingLocation = crankBearingLocation.add(new Vec2((float) be.bearingPos.getZ(), (float) be.bearingPos.getY()));

            dstY = crankBearingLocation.y - sin - 1.25 - pos.getY();
            dstX = crankBearingLocation.x - cos - 0.5 - pos.getX();
            dstZ = crankBearingLocation.x - cos - 0.5 - pos.getZ();
        }

        if (isXAxis) {
            crank.translate(0.5, 1.25, 0).rotateZDegrees(angle);
            rod.translate(0.5, 1.25, 0).translate(cos, sin, 0).rotateZDegrees((float) (Math.atan2(dstY, dstX) * 180 / Math.PI - 90));
        } else {
            crank.translate(0, 1.25, 0.5).rotateYDegrees(90).rotateZDegrees(angle);
            rod.translate(0, 1.25, 0.5).translate(0, sin, cos).rotateYDegrees(90).rotateZDegrees((float) (Math.atan2(dstZ, dstY) * 180 / Math.PI));
        }

        state.rod = rod.light(state.lightCoords).extractRenderState();
        state.crank = crank.light(state.lightCoords).extractRenderState();
    }

    @Override
    public void submit(SingleKineticRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        PumpjackCrankRenderState state = (PumpjackCrankRenderState) renderState;
        if (state.rod != null)
            state.rod.submit(matrices, queue);
        if (state.crank != null)
            state.crank.submit(matrices, queue);
        super.submit(renderState, matrices, queue, cameraState);
    }

    public static class PumpjackCrankRenderState extends SingleKineticRenderState {
        public @Nullable SuperByteBufferRenderState crank;
        public @Nullable SuperByteBufferRenderState rod;
    }
}
