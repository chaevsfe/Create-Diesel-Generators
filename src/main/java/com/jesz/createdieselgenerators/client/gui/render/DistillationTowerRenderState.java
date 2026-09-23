package com.jesz.createdieselgenerators.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public record DistillationTowerRenderState(Matrix3x2f pose, int tiers, int x0, int y0,
                                           ScreenRectangle bounds) implements PictureInPictureRenderState {
    public static final int WIDTH = 36;
    public static final int SCALE = 23;
    public static final int PADDING = 16;

    public DistillationTowerRenderState(Matrix3x2f pose, int tiers, int x, int y) {
        this(pose, tiers, x, y, new ScreenRectangle(x, y, WIDTH, height(tiers)).transformMaxBounds(pose));
    }

    public static int height(int tiers) {
        return tiers * SCALE + PADDING;
    }

    @Override
    public int x1() {
        return x0 + WIDTH;
    }

    @Override
    public int y1() {
        return y0 + height(tiers);
    }

    @Override
    public float scale() {
        return SCALE;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return null;
    }
}
