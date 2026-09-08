package com.jesz.createdieselgenerators.content.diesel_engine.normal;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.client.render.EngineUpgradeRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineBlock.FACING;

public class DieselEngineRenderer extends ShaftRenderer {
    private static final PartialModel[] HORIZONTAL = {
            CDGPartialModels.ENGINE_PISTONS_0, CDGPartialModels.ENGINE_PISTONS_1, CDGPartialModels.ENGINE_PISTONS_2,
            CDGPartialModels.ENGINE_PISTONS_3, CDGPartialModels.ENGINE_PISTONS_4
    };
    private static final PartialModel[] VERTICAL = {
            CDGPartialModels.ENGINE_PISTONS_VERTICAL_0, CDGPartialModels.ENGINE_PISTONS_VERTICAL_1, CDGPartialModels.ENGINE_PISTONS_VERTICAL_2,
            CDGPartialModels.ENGINE_PISTONS_VERTICAL_3, CDGPartialModels.ENGINE_PISTONS_VERTICAL_4
    };

    public DieselEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static int pistonFrame(KineticBlockEntity be) {
        return (int) (Math.abs(KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(),
                KineticBlockEntityRenderer.getRotationAxisOf(be)) * 180 / Math.PI) * 3 % 360) / 36;
    }

    public static PartialModel pistons(PartialModel[] frames, int angle) {
        return switch (angle) {
            case 9 -> frames[1];
            case 8 -> frames[2];
            case 7 -> frames[3];
            case 6, 5 -> frames[4];
            case 4 -> frames[3];
            case 3 -> frames[2];
            case 2 -> frames[1];
            default -> frames[0];
        };
    }

    @Override
    public DieselEngineRenderState createRenderState() {
        return new DieselEngineRenderState();
    }

    @Override
    public void extractRenderState(KineticBlockEntity be, SingleKineticRenderState renderState, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, renderState, partialTicks, cameraPos, crumblingOverlay);
        DieselEngineRenderState state = (DieselEngineRenderState) renderState;
        DieselEngineBlockEntity engine = (DieselEngineBlockEntity) be;
        BlockState blockState = state.blockState;
        int angle = pistonFrame(be);
        state.upgrade = EngineUpgradeRenderer.extract(engine, engine.upgrade, state.lightCoords);
        Direction facing = blockState.getValue(FACING);
        if (facing.getAxis().isHorizontal())
            state.pistons = CachedBuffers.partial(pistons(HORIZONTAL, angle), blockState)
                    .center()
                    .rotateYDegrees(facing.toYRot())
                    .uncenter()
                    .light(state.lightCoords)
                    .extractRenderState();
        else
            state.pistons = CachedBuffers.partial(pistons(VERTICAL, angle), blockState)
                    .center()
                    .rotateYDegrees(facing == Direction.DOWN ? 180 : 270)
                    .rotateZDegrees(facing == Direction.DOWN ? 180 : 0)
                    .uncenter()
                    .light(state.lightCoords)
                    .extractRenderState();
    }

    @Override
    public void submit(SingleKineticRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        DieselEngineRenderState state = (DieselEngineRenderState) renderState;
        if (state.upgrade != null)
            state.upgrade.submit(RenderTypes.cutoutMovingBlock(), matrices, queue);
        if (state.pistons != null)
            state.pistons.submit(matrices, queue);
        super.submit(renderState, matrices, queue, cameraState);
    }

    public static class DieselEngineRenderState extends SingleKineticRenderState {
        public @Nullable SuperByteBufferRenderState pistons;
        public @Nullable SuperByteBufferRenderState upgrade;
    }
}
