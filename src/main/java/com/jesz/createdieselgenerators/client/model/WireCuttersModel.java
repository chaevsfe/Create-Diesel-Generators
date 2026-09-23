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
public class WireCuttersModel implements ItemModel {
    public static final Identifier ID = CreateDieselGenerators.rl("model/wire_cutters");
    public static final Identifier CLOSED_ID = CreateDieselGenerators.rl("item/wire_cutters");
    public static final Identifier OPEN_ID = CreateDieselGenerators.rl("item/wire_cutters_cut");

    private final List<BakedQuad> closedQuads;
    private final ModelRenderProperties settings;
    private final Supplier<Vector3fc[]> closedExtents;
    private final List<BakedQuad> openQuads;
    private final Supplier<Vector3fc[]> openExtents;

    public WireCuttersModel(List<BakedQuad> closedQuads, ModelRenderProperties settings, List<BakedQuad> openQuads) {
        this.closedQuads = closedQuads;
        this.settings = settings;
        this.closedExtents = ItemModelParts.extents(closedQuads);
        this.openQuads = openQuads;
        this.openExtents = ItemModelParts.extents(openQuads);
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext transformType, ClientLevel level, ItemOwner owner, int seed) {
        state.appendModelIdentityElement(this);
        if (!stack.has(CDGDataComponents.PROCESSING_ITEM) || Minecraft.getInstance().player == null) {
            var layer = ItemModelRenderHelper.submitQuads(state, settings, transformType, closedQuads);
            layer.setExtents(closedExtents);
            return;
        }
        state.setAnimated();

        float time = ((AnimationTickHolder.getTicks() + AnimationTickHolder.getPartialTicks()) % 10) / 10;
        boolean closed = time > 0.5;
        var layer = ItemModelRenderHelper.submitQuads(state, settings, transformType, closed ? closedQuads : openQuads);
        layer.setExtents(closed ? closedExtents : openExtents);
        layer.setLocalTransform(Local.centered(new Matrix4f().translate(0, 0, 0.1f).rotateY(Local.rad(32))));

        ItemStack processing = stack.get(CDGDataComponents.PROCESSING_ITEM).item();
        state.appendModelIdentityElement(processing.getItem());
        Matrix4f itemTransform = new Matrix4f()
                .translate(0.1f, 0.2f, 0)
                .rotateZ(Local.rad((AnimationTickHolder.getTicks() + 5) / 10 * -30));
        Local.appendItem(state, resolver, processing, ItemDisplayContext.GUI, level, owner, seed, Local.centered(itemTransform));
    }

    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(CLOSED_ID);
            resolver.markDependency(OPEN_ID);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transform) {
            var baker = context.blockModelBaker();
            var parts = ItemModelParts.of(baker, CLOSED_ID);
            return new WireCuttersModel(parts.quads(), parts.properties(), ItemModelParts.quads(baker, OPEN_ID));
        }
    }
}
