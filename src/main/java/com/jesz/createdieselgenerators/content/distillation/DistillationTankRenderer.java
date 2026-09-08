package com.jesz.createdieselgenerators.content.distillation;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.FluidRenderHelper;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.foundation.fluid.FluidTank;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidStateModelSet;
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

import java.util.ArrayList;
import java.util.List;

public class DistillationTankRenderer implements BlockEntityRenderer<DistillationTankBlockEntity, DistillationTankRenderer.DistillationTankRenderState> {
    private final FluidStateModelSet fluidStateModelSet;

    public DistillationTankRenderer(BlockEntityRendererProvider.Context context) {
        this.fluidStateModelSet = context.blockModelResolver().modelManager.getFluidStateModelSet();
    }

    @Override
    public DistillationTankRenderState createRenderState() {
        return new DistillationTankRenderState();
    }

    @Override
    public void extractRenderState(DistillationTankBlockEntity be, DistillationTankRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        state.gauges.clear();
        state.fluid = null;
        if (!be.isController())
            return;

        if (be.isBottom())
            extractGauges(be, state, partialTicks);

        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        float capHeight = 0.25f;
        float tankHullWidth = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.getHeight() - 2 * capHeight - minPuddleHeight;

        float level2 = fluidLevel.getValue(partialTicks);
        if (level2 < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level2 * totalHeight, 0, totalHeight);

        FluidTank tank = be.tankInventory;
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack.isEmpty())
            return;

        float xMin = tankHullWidth;
        float xMax = xMin + be.getWidth() - 2 * tankHullWidth;
        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
        float yMax = yMin + clampedLevel;
        float zMin = tankHullWidth;
        float zMax = zMin + be.getWidth() - 2 * tankHullWidth;

        state.fluidTranslate = clampedLevel - totalHeight;
        state.fluid = FluidRenderHelper.extractFluidRenderState((net.minecraft.client.renderer.block.BlockAndTintGetter) level, state.blockPos, fluidStateModelSet, fluidStack.getFluid(),
                fluidStack.getComponentChanges(), xMin, yMin, zMin, xMax, yMax, zMax, state.lightCoords, false, true);
    }

    private void extractGauges(DistillationTankBlockEntity be, DistillationTankRenderState state, float partialTicks) {
        BlockState blockState = state.blockState;
        state.offset = be.getWidth() / 2f;

        float dialPivotY = 6f / 16;
        float dialPivotZ = 8f / 16;
        float progress = Mth.clamp(be.currentRecipe == null ? be.progress
                : (be.processingTime - partialTicks) / be.currentRecipe.getProcessingDuration(), 0, 1);

        for (Direction d : Iterate.horizontalDirections) {
            state.gauges.add(CachedBuffers.partial(CDGPartialModels.DISTILLATION_GAUGE, blockState)
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
                    .rotateXDegrees(-145 * progress + 90)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(state.lightCoords)
                    .extractRenderState());
        }
    }

    @Override
    public void submit(DistillationTankRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (!state.gauges.isEmpty()) {
            matrices.pushPose();
            matrices.translate(state.offset, 0.5f, state.offset);
            for (SuperByteBufferRenderState gauge : state.gauges)
                gauge.submit(RenderTypes.cutoutMovingBlock(), matrices, queue);
            matrices.popPose();
        }
        if (state.fluid != null) {
            matrices.pushPose();
            matrices.translate(0, state.fluidTranslate, 0);
            state.fluid.submit(matrices, queue);
            matrices.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public static class DistillationTankRenderState extends BlockEntityRenderState {
        public final List<SuperByteBufferRenderState> gauges = new ArrayList<>();
        public @Nullable FluidRenderHelper.FluidRenderState fluid;
        public float fluidTranslate;
        public float offset;
    }
}
