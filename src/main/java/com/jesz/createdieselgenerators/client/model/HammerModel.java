package com.jesz.createdieselgenerators.client.model;

import com.jesz.createdieselgenerators.CDGDataComponents;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.flywheel.lib.model.baked.ItemModelRenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class HammerModel implements ItemModel {
    public static final Identifier ID = CreateDieselGenerators.rl("model/hammer");
    public static final Identifier ITEM_ID = CreateDieselGenerators.rl("item/hammer");

    private final List<BakedQuad> quads;
    private final ModelRenderProperties settings;
    private final Supplier<Vector3fc[]> extents;

    public HammerModel(List<BakedQuad> quads, ModelRenderProperties settings) {
        this.quads = quads;
        this.settings = settings;
        this.extents = ItemModelParts.extents(quads);
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext transformType, ClientLevel level, ItemOwner owner, int seed) {
        state.appendModelIdentityElement(this);
        var tool = ItemModelRenderHelper.submitQuads(state, settings, transformType, quads);
        tool.setExtents(extents);

        if (!stack.has(CDGDataComponents.PROCESSING_ITEM) || Minecraft.getInstance().player == null)
            return;
        state.setAnimated();

        float time = ((AnimationTickHolder.getTicks() + AnimationTickHolder.getPartialTicks()) % 10) / 10 - 0.5f;
        float swing = Math.abs(time * time * time);
        ItemStack processing = stack.get(CDGDataComponents.PROCESSING_ITEM).item();
        state.appendModelIdentityElement(processing.getItem());

        Matrix4f toolTransform = new Matrix4f();
        Matrix4f itemTransform = new Matrix4f();
        if (transformType.firstPerson()) {
            float flip = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
            toolTransform.translate(0, 0, flip).translate(0, 0, -0.6f).rotateY(Local.rad(45)).translate(-0.5f, -0.5f, -0.5f)
                    .rotateZ(Local.rad(swing * 400)).translate(0.5f, 0.5f, 0.5f);
            itemTransform.translate(0, 0, flip).translate(-0.5f, 0.4f, 0)
                    .translate((float) (Math.cos(time * -Math.PI) / -10), 0, 0).rotateY(Local.rad(-45));
        } else if (transformType.name().startsWith("THIRD_PERSON")) {
            toolTransform.rotateY(Local.rad(90)).translate(0, 0, -0.7f).scale(0.75f, 0.75f, 1.1f).rotateY(Local.rad(77))
                    .translate(-0.5f, -0.5f, -0.5f).rotateZ(Local.rad(swing * -180 + 80)).translate(0.5f, 0.5f, 0.5f);
            itemTransform.translate(-0.2f, 0.4f, 0).scale(0.75f).rotateY(Local.rad(77));
        } else {
            toolTransform.translate(0.5f, -0.2f, 0).scale(0.75f, 0.75f, 1.1f).translate(-0.5f, -0.5f, -0.5f)
                    .rotateZ(Local.rad(swing * 300)).translate(0.5f, 0.5f, 0.5f);
        }

        tool.setLocalTransform(Local.centered(toolTransform));
        Local.appendItem(state, resolver, processing, ItemDisplayContext.NONE, level, owner, seed, Local.centered(itemTransform));
    }

    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(ITEM_ID);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transform) {
            var parts = ItemModelParts.of(context.blockModelBaker(), ITEM_ID);
            return new HammerModel(parts.quads(), parts.properties());
        }
    }
}
