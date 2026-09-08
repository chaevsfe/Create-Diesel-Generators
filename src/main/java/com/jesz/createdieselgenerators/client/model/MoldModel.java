package com.jesz.createdieselgenerators.client.model;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.molds.MoldItem;
import com.jesz.createdieselgenerators.content.molds.MoldType;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.client.flywheel.lib.model.baked.ItemModelRenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class MoldModel implements ItemModel {
    public static final Identifier ID = CreateDieselGenerators.rl("model/mold");
    public static final Identifier ITEM_ID = CreateDieselGenerators.rl("item/mold");

    private final List<BakedQuad> emptyQuads;
    private final ModelRenderProperties settings;
    private final Supplier<Vector3fc[]> emptyExtents;
    private final Map<Identifier, List<BakedQuad>> typeQuads;
    private final Map<Identifier, Supplier<Vector3fc[]>> typeExtents = new HashMap<>();

    public MoldModel(List<BakedQuad> emptyQuads, ModelRenderProperties settings, Map<Identifier, List<BakedQuad>> typeQuads) {
        this.emptyQuads = emptyQuads;
        this.settings = settings;
        this.emptyExtents = ItemModelParts.extents(emptyQuads);
        this.typeQuads = typeQuads;
        typeQuads.forEach((id, quads) -> typeExtents.put(id, ItemModelParts.extents(quads)));
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext transformType, ClientLevel level, ItemOwner owner, int seed) {
        state.appendModelIdentityElement(this);
        MoldType type = MoldItem.getMold(stack);
        List<BakedQuad> quads = emptyQuads;
        Supplier<Vector3fc[]> extents = emptyExtents;
        if (type != null && typeQuads.containsKey(type.getId())) {
            quads = typeQuads.get(type.getId());
            extents = typeExtents.get(type.getId());
            state.appendModelIdentityElement(type.getId());
        }
        var layer = ItemModelRenderHelper.submitQuads(state, settings, transformType, quads);
        layer.setExtents(extents);
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
            for (MoldType type : MoldType.types)
                resolver.markDependency(type.getModelId());
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transform) {
            var baker = context.blockModelBaker();
            var parts = ItemModelParts.of(baker, ITEM_ID);
            Map<Identifier, List<BakedQuad>> byType = new HashMap<>();
            for (MoldType type : MoldType.types)
                byType.put(type.getId(), ItemModelParts.quads(baker, type.getModelId()));
            return new MoldModel(parts.quads(), parts.properties(), byType);
        }
    }
}
