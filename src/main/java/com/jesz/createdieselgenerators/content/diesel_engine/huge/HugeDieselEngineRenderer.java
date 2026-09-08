package com.jesz.createdieselgenerators.content.diesel_engine.huge;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.client.render.EngineUpgradeRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlock.FACING;

public class HugeDieselEngineRenderer implements BlockEntityRenderer<HugeDieselEngineBlockEntity, HugeDieselEngineRenderer.HugeDieselEngineRenderState> {
    public HugeDieselEngineRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public HugeDieselEngineRenderState createRenderState() {
        return new HugeDieselEngineRenderState();
    }

    @Override
    public void extractRenderState(HugeDieselEngineBlockEntity be, HugeDieselEngineRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.piston = null;
        state.linkage = null;
        state.connector = null;
        state.upgrade = EngineUpgradeRenderer.extract(be, be.upgrade, state.lightCoords);

        if (VisualizationManager.supportsVisualization(level))
            return;

        BlockState blockState = state.blockState;
        Direction facing = blockState.getValue(FACING);
        Direction.Axis facingAxis = facing.getAxis();
        Float angle = be.getTargetAngle();
        PoweredEngineShaftBlockEntity shaft = angle == null ? null : be.getShaft();
        if (angle == null || shaft == null) {
            state.piston = transformed(CDGPartialModels.ENGINE_PISTON, blockState, facing, false)
                    .translate(0, 0.53475, 0)
                    .light(state.lightCoords)
                    .extractRenderState();
            return;
        }

        Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(shaft);
        boolean roll90 = facingAxis.isHorizontal() && axis == Direction.Axis.Y || facingAxis.isVertical() && axis == Direction.Axis.Z;
        float shaftR = facing == Direction.DOWN ? -90 : facing == Direction.UP ? 90 : facing == Direction.WEST ? -90 : facing == Direction.EAST ? 90 : 0;
        if (roll90)
            shaftR = facing == Direction.NORTH ? 180 : facing == Direction.SOUTH ? 0 : facing == Direction.EAST ? -90 : facing == Direction.WEST ? 90 : 0;
        angle += (float) (shaftR * Math.PI / 180);

        float sine = Mth.sin(angle) * (facingAxis == Direction.Axis.Y ? -1 : 1);
        float sine2 = Mth.sin(angle - Mth.HALF_PI) * (facingAxis == Direction.Axis.Y ? -1 : 1);
        float piston = ((1 - sine) / 4) + 0.4375f;

        state.piston = transformed(CDGPartialModels.ENGINE_PISTON, blockState, facing, roll90)
                .translate(0, piston, 0)
                .light(state.lightCoords)
                .extractRenderState();

        state.linkage = transformed(CDGPartialModels.ENGINE_PISTON_LINKAGE, blockState, facing, roll90)
                .center()
                .translate(0, 1, 0)
                .uncenter()
                .translate(0, piston, 0)
                .translate(0, 4 / 16f, 8 / 16f)
                .rotateXDegrees(sine2 * 23f)
                .translate(0, -4 / 16f, -8 / 16f)
                .light(state.lightCoords)
                .extractRenderState();

        if (shaft.isEngineForConnectorDisplay(state.blockPos))
            state.connector = transformed(CDGPartialModels.ENGINE_PISTON_CONNECTOR, blockState, facing, roll90)
                    .translate(0, 2, 0)
                    .center()
                    .rotateX(-angle + Mth.HALF_PI)
                    .uncenter()
                    .light(state.lightCoords)
                    .extractRenderState();
    }

    private static SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing, boolean roll90) {
        return CachedBuffers.partial(model, blockState)
                .center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing) + 90)
                .rotateYDegrees(roll90 ? -90 : 0)
                .uncenter();
    }

    @Override
    public void submit(HugeDieselEngineRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.upgrade != null)
            state.upgrade.submit(RenderTypes.cutoutMovingBlock(), matrices, queue);
        if (state.piston != null)
            state.piston.submit(matrices, queue);
        if (state.linkage != null)
            state.linkage.submit(matrices, queue);
        if (state.connector != null)
            state.connector.submit(matrices, queue);
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    public static class HugeDieselEngineRenderState extends BlockEntityRenderState {
        public @Nullable SuperByteBufferRenderState piston;
        public @Nullable SuperByteBufferRenderState linkage;
        public @Nullable SuperByteBufferRenderState connector;
        public @Nullable SuperByteBufferRenderState upgrade;
    }
}
