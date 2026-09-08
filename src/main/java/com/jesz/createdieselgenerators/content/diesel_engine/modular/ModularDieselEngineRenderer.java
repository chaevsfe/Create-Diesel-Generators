package com.jesz.createdieselgenerators.content.diesel_engine.modular;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.client.render.EngineUpgradeRenderer;
import com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlock.FACING;

public class ModularDieselEngineRenderer extends ShaftRenderer {
    private static final PartialModel[] PISTONS = {
            CDGPartialModels.MODULAR_ENGINE_PISTONS_0, CDGPartialModels.MODULAR_ENGINE_PISTONS_1, CDGPartialModels.MODULAR_ENGINE_PISTONS_2,
            CDGPartialModels.MODULAR_ENGINE_PISTONS_3, CDGPartialModels.MODULAR_ENGINE_PISTONS_4
    };

    public ModularDieselEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ModularDieselEngineRenderState createRenderState() {
        return new ModularDieselEngineRenderState();
    }

    @Override
    public void extractRenderState(KineticBlockEntity be, SingleKineticRenderState renderState, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, renderState, partialTicks, cameraPos, crumblingOverlay);
        ModularDieselEngineRenderState state = (ModularDieselEngineRenderState) renderState;
        ModularDieselEngineBlockEntity engine = (ModularDieselEngineBlockEntity) be;
        BlockState blockState = state.blockState;
        int angle = DieselEngineRenderer.pistonFrame(be);
        ModularDieselEngineBlockEntity controller = engine.getControllerBE();
        state.upgrade = EngineUpgradeRenderer.extract(engine, Objects.requireNonNullElse(controller, engine).upgrade, state.lightCoords);
        state.pistons = CachedBuffers.partial(DieselEngineRenderer.pistons(PISTONS, angle), blockState)
                .center()
                .rotateYDegrees(blockState.getValue(FACING).toYRot())
                .uncenter()
                .light(state.lightCoords)
                .extractRenderState();
    }

    @Override
    public void submit(SingleKineticRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        ModularDieselEngineRenderState state = (ModularDieselEngineRenderState) renderState;
        if (state.upgrade != null)
            state.upgrade.submit(RenderTypes.cutoutMovingBlock(), matrices, queue);
        if (state.pistons != null)
            state.pistons.submit(matrices, queue);
        super.submit(renderState, matrices, queue, cameraState);
    }

    public static class ModularDieselEngineRenderState extends SingleKineticRenderState {
        public @Nullable SuperByteBufferRenderState pistons;
        public @Nullable SuperByteBufferRenderState upgrade;
    }
}
