package com.jesz.createdieselgenerators.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class Local {
    private Local() {
    }

    public static float rad(float degrees) {
        return degrees * ((float) Math.PI / 180);
    }

    public static Matrix4f centered(Matrix4fc pose) {
        return new Matrix4f().translate(0.5f, 0.5f, 0.5f).mul(pose).translate(-0.5f, -0.5f, -0.5f);
    }

    public static void appendItem(ItemStackRenderState state, ItemModelResolver resolver, ItemStack stack,
                                  ItemDisplayContext context, ClientLevel level, ItemOwner owner, int seed, Matrix4fc transform) {
        if (stack.isEmpty())
            return;
        ItemStackRenderState sub = new ItemStackRenderState();
        resolver.updateForTopItem(sub, stack, context, level, owner, seed);
        if (sub.isEmpty())
            return;
        Matrix4f placed = new Matrix4f(transform).translate(0.5f, 0.5f, 0.5f);
        ItemStackRenderState.LayerRenderState layer = state.newLayer();
        layer.setupSpecialModel(SubItemRenderer.INSTANCE, new SubItemRenderer.Argument(sub, placed));
        List<Vector3fc> points = new ArrayList<>();
        sub.visitExtents(point -> points.add(placed.transformPosition(point, new Vector3f())));
        Vector3fc[] extents = points.toArray(Vector3fc[]::new);
        layer.setExtents(() -> extents);
    }
}
