package com.jesz.createdieselgenerators.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@Environment(EnvType.CLIENT)
public record MoldBasinLayout(List<Entry> entries) {
    @Environment(EnvType.CLIENT)
    public record Entry(ItemStackRenderState renderState, Vec3 offset, boolean mold) {
    }
}
