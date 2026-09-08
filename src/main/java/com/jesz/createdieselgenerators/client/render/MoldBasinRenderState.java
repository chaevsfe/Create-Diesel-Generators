package com.jesz.createdieselgenerators.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface MoldBasinRenderState {
    @Nullable
    MoldBasinLayout createdieselgenerators$getMoldLayout();

    void createdieselgenerators$setMoldLayout(@Nullable MoldBasinLayout layout);
}
