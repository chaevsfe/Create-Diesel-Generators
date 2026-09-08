package com.jesz.createdieselgenerators.content.pumpjack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.contraptions.bearing.IBearingBlockEntity;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class NoShaftBearingRenderer<T extends KineticBlockEntity & IBearingBlockEntity>
        implements BlockEntityRenderer<T, NoShaftBearingRenderer.NoShaftBearingRenderState> {
    public NoShaftBearingRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public NoShaftBearingRenderState createRenderState() {
        return new NoShaftBearingRenderState();
    }

    @Override
    public void extractRenderState(T be, NoShaftBearingRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.top = null;
        state.topAngle = null;
        state.upAngle = null;
        state.eastAngle = null;
        if (VisualizationManager.supportsVisualization(level))
            return;

        Direction facing = state.blockState.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = facing.getAxis();
        float interpolatedAngle = be.getInterpolatedAngle(partialTicks - 1);
        state.topAngle = KineticBlockEntityRenderer.getRadiansRotateAngle((float) (interpolatedAngle / 180 * Math.PI), axis.getPositive());
        state.eastAngle = KineticBlockEntityRenderer.getEastRotateAngle(-90 - AngleHelper.verticalAngle(facing));
        if (axis != Direction.Axis.Y)
            state.upAngle = KineticBlockEntityRenderer.getUpRotateAngle(AngleHelper.horizontalAngle(facing.getOpposite()));

        PartialModel top = be.isWoodenTop() ? AllPartialModels.BEARING_TOP_WOODEN : AllPartialModels.BEARING_TOP;
        state.top = CachedBuffers.partial(top, state.blockState)
                .cardinalLighting(SmartBlockEntityRenderer.getCardinalLighting(level))
                .light(state.lightCoords)
                .extractRenderState();
    }

    @Override
    public void submit(NoShaftBearingRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.top == null)
            return;
        matrices.pushPose();
        if (state.topAngle != null)
            matrices.rotateAround(state.topAngle, 0.5f, 0.5f, 0.5f);
        if (state.upAngle != null)
            matrices.rotateAround(state.upAngle, 0.5f, 0.5f, 0.5f);
        if (state.eastAngle != null)
            matrices.rotateAround(state.eastAngle, 0.5f, 0.5f, 0.5f);
        state.top.submit(matrices, queue);
        matrices.popPose();
    }

    public static class NoShaftBearingRenderState extends BlockEntityRenderState {
        public @Nullable SuperByteBufferRenderState top;
        public @Nullable Quaternionf topAngle;
        public @Nullable Quaternionf upAngle;
        public @Nullable Quaternionf eastAngle;
    }
}
