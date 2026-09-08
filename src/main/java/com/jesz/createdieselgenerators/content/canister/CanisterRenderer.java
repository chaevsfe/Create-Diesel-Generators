package com.jesz.createdieselgenerators.content.canister;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CanisterRenderer extends SmartBlockEntityRenderer<CanisterBlockEntity, CanisterRenderer.CanisterRenderState> {
    public CanisterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public CanisterRenderState createRenderState() {
        return new CanisterRenderState();
    }

    @Override
    public void extractRenderState(CanisterBlockEntity be, CanisterRenderState state, float partialTicks, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.enchanted = null;
        if (be.capacityEnchantLevel == 0)
            return;
        state.enchanted = CachedBuffers.block(state.blockState)
                .light(state.lightCoords)
                .extractRenderState();
    }

    @Override
    public void submit(CanisterRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        super.submit(state, matrices, queue, cameraState);
        if (state.enchanted == null)
            return;
        state.enchanted.submit(RenderTypes.cutoutMovingBlock(), matrices, queue);
        state.enchanted.submit(RenderTypes.glint(), matrices, queue);
    }

    public static class CanisterRenderState extends SmartRenderState {
        public @Nullable SuperByteBufferRenderState enchanted;
    }
}
