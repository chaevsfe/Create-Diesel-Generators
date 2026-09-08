package com.jesz.createdieselgenerators.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class SubItemRenderer implements SpecialModelRenderer<SubItemRenderer.Argument> {
    public static final SubItemRenderer INSTANCE = new SubItemRenderer();

    public record Argument(ItemStackRenderState state, Matrix4fc transform) {
    }

    private SubItemRenderer() {
    }

    @Override
    public void submit(Argument argument, PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, boolean glint, int tint) {
        matrices.pushPose();
        matrices.mulPose(argument.transform());
        argument.state().submit(matrices, queue, light, overlay, tint);
        matrices.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
    }

    @Override
    public Argument extractArgument(ItemStack stack) {
        return null;
    }
}
