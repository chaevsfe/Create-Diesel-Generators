package com.jesz.createdieselgenerators.content.turret;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChemicalTurretRenderer extends SmartBlockEntityRenderer<ChemicalTurretBlockEntity, ChemicalTurretRenderer.ChemicalTurretRenderState> {
    public ChemicalTurretRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ChemicalTurretRenderState createRenderState() {
        return new ChemicalTurretRenderState();
    }

    @Override
    public void extractRenderState(ChemicalTurretBlockEntity be, ChemicalTurretRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.parts.clear();
        BlockState blockState = state.blockState;
        int light = state.lightCoords;

        float horizontalRotation = AngleHelper.angleLerp(partialTicks, be.oldHorizontalRotation, be.horizontalRotation);
        float verticalRotation = AngleHelper.angleLerp(partialTicks, be.oldVerticalRotation, be.verticalRotation);
        float cogAngle = KineticBlockEntityRenderer.getAngleForBe(be, state.blockPos, KineticBlockEntityRenderer.getRotationAxisOf(be));

        state.parts.add(CachedBuffers.partial(CDGPartialModels.CHEMICAL_TURRET_COG, blockState)
                .rotateCentered(cogAngle, KineticBlockEntityRenderer.getRotationAxisOf(be).getPositive())
                .light(light)
                .color(KineticBlockEntityRenderer.getTintColor(be))
                .extractRenderState());

        state.parts.add(CachedBuffers.partial(CDGPartialModels.CHEMICAL_TURRET_CONNECTOR, blockState)
                .center()
                .rotateYDegrees(horizontalRotation)
                .uncenter()
                .light(light)
                .extractRenderState());
        state.parts.add(head(CDGPartialModels.CHEMICAL_TURRET_BODY, blockState, horizontalRotation, verticalRotation).light(light).extractRenderState());
        if (be.lighterUpgrade)
            state.parts.add(head(CDGPartialModels.CHEMICAL_TURRET_LIGHTER, blockState, horizontalRotation, verticalRotation).light(light).extractRenderState());
        state.parts.add(head(CDGPartialModels.CHEMICAL_TURRET_SMALL_COG, blockState, horizontalRotation, verticalRotation)
                .rotateZDegrees(Mth.lerp(partialTicks, be.lastCogRotation, be.cogRotation))
                .light(light)
                .extractRenderState());
    }

    private static SuperByteBuffer head(com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel model, BlockState blockState, float horizontalRotation, float verticalRotation) {
        return CachedBuffers.partial(model, blockState)
                .center()
                .rotateYDegrees(horizontalRotation + 180)
                .uncenter()
                .translate(0.5, 1.3125, 0.125)
                .rotateXDegrees(verticalRotation);
    }

    @Override
    public void submit(ChemicalTurretRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        super.submit(state, matrices, queue, cameraState);
        for (SuperByteBufferRenderState part : state.parts)
            part.submit(matrices, queue);
    }

    public static class ChemicalTurretRenderState extends SmartRenderState {
        public final List<SuperByteBufferRenderState> parts = new ArrayList<>();
    }
}
