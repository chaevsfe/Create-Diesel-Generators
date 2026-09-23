package com.jesz.createdieselgenerators.client.gui.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.gui.render.BlockBakedQuadOutput;
import com.zurrtum.create.client.flywheel.lib.model.baked.ModelRenderHelper;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.flywheel.lib.model.baked.SinglePosVirtualBlockGetter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public abstract class CDGGuiBlockRenderer<T extends PictureInPictureRenderState> extends PictureInPictureRenderer<T> {
    private final BlockBakedQuadOutput output;
    private SinglePosVirtualBlockGetter world;

    protected CDGGuiBlockRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        output = new BlockBakedQuadOutput(bufferSource);
    }

    @Override
    protected final void renderToTexture(T state, PoseStack matrices) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        output.setPoseStack(matrices);
        world = SinglePosVirtualBlockGetter.createFullBright();
        render(state, matrices);
        output.clear();
        world = null;
    }

    protected abstract void render(T state, PoseStack matrices);

    protected void block(BlockState state) {
        draw(state, Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state));
    }

    protected void partial(PartialModel partial, BlockState state) {
        draw(state, partial.get());
    }

    private void draw(BlockState state, BlockStateModel model) {
        world.blockState(state);
        output.updateBuffer(model);
        ModelRenderHelper.getHelper(output).tesselateBlock(0, 0, 0, world, BlockPos.ZERO, state, model, 42L);
    }
}
