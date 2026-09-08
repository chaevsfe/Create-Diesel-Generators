package com.jesz.createdieselgenerators.content.bulk_fermenter;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BulkFermenterRenderer implements BlockEntityRenderer<BulkFermenterBlockEntity, BulkFermenterRenderer.BulkFermenterRenderState> {
    public BulkFermenterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BulkFermenterRenderState createRenderState() {
        return new BulkFermenterRenderState();
    }

    @Override
    public void extractRenderState(BulkFermenterBlockEntity be, BulkFermenterRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.gauges.clear();
        if (!be.isController())
            return;

        BlockState blockState = state.blockState;
        state.offset = be.getWidth() / 2f;

        float dialPivotY = 6f / 16;
        float dialPivotZ = 8f / 16;
        float progress = be.currentRecipe == null ? 0 : (float) Mth.clamp(
                Mth.lerp(partialTicks, be.processingTime + Math.sqrt(be.width * be.height), be.processingTime) / be.currentRecipe.getProcessingDuration(), 0, 1);

        for (Direction d : Iterate.horizontalDirections) {
            state.gauges.add(CachedBuffers.partial(CDGPartialModels.BULK_FERMENTER_GAUGE, blockState)
                    .rotateYDegrees(d.toYRot())
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .light(state.lightCoords)
                    .extractRenderState());
            state.gauges.add(CachedBuffers.partial(AllPartialModels.BOILER_GAUGE_DIAL, blockState)
                    .rotateYDegrees(d.toYRot())
                    .uncenter()
                    .translate(be.width / 2f - 6 / 16f, 0, 0)
                    .translate(0, dialPivotY, dialPivotZ)
                    .rotateXDegrees(-180 * progress + 90)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(state.lightCoords)
                    .extractRenderState());
        }
    }

    @Override
    public void submit(BulkFermenterRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.gauges.isEmpty())
            return;
        matrices.pushPose();
        matrices.translate(state.offset, 0.5f, state.offset);
        for (SuperByteBufferRenderState gauge : state.gauges)
            gauge.submit(RenderTypes.cutoutMovingBlock(), matrices, queue);
        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public static class BulkFermenterRenderState extends BlockEntityRenderState {
        public final List<SuperByteBufferRenderState> gauges = new ArrayList<>();
        public float offset;
    }
}
