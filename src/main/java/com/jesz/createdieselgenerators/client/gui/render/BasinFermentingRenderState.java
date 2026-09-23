package com.jesz.createdieselgenerators.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public record BasinFermentingRenderState(Matrix3x2f pose, int x0, int y0,
                                         ScreenRectangle bounds) implements PictureInPictureRenderState {
    public BasinFermentingRenderState(Matrix3x2f pose, int x, int y) {
        this(pose, x, y, new ScreenRectangle(x, y, 30, 80).transformMaxBounds(pose));
    }

    @Override
    public int x1() {
        return x0 + 30;
    }

    @Override
    public int y1() {
        return y0 + 80;
    }

    @Override
    public float scale() {
        return 23;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return null;
    }
}
