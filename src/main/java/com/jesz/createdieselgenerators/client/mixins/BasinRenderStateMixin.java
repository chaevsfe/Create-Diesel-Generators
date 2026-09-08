package com.jesz.createdieselgenerators.client.mixins;

import com.jesz.createdieselgenerators.client.render.MoldBasinLayout;
import com.jesz.createdieselgenerators.client.render.MoldBasinRenderState;
import com.zurrtum.create.client.content.processing.basin.BasinRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BasinRenderer.BasinRenderState.class)
public class BasinRenderStateMixin implements MoldBasinRenderState {
    @Unique
    @Nullable
    private MoldBasinLayout createdieselgenerators$moldLayout;

    @Nullable
    @Override
    public MoldBasinLayout createdieselgenerators$getMoldLayout() {
        return createdieselgenerators$moldLayout;
    }

    @Override
    public void createdieselgenerators$setMoldLayout(@Nullable MoldBasinLayout layout) {
        this.createdieselgenerators$moldLayout = layout;
    }
}
