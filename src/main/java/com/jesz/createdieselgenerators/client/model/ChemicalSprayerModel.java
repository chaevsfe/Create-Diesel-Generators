package com.jesz.createdieselgenerators.client.model;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.flywheel.lib.model.baked.ItemModelRenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
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
public class ChemicalSprayerModel implements ItemModel {
    public static final Identifier ID = CreateDieselGenerators.rl("model/chemical_sprayer");
    public static final Identifier LIGHTER_ID = CreateDieselGenerators.rl("model/chemical_sprayer_lighter");
    public static final Identifier COG_ID = CreateDieselGenerators.rl("item/chemical_sprayer/cog");

    private final List<BakedQuad> bodyQuads;
    private final ModelRenderProperties settings;
    private final Supplier<Vector3fc[]> bodyExtents;
    private final List<BakedQuad> cogQuads;
    private final Supplier<Vector3fc[]> cogExtents;

    public ChemicalSprayerModel(List<BakedQuad> bodyQuads, ModelRenderProperties settings, List<BakedQuad> cogQuads) {
        this.bodyQuads = bodyQuads;
        this.settings = settings;
        this.bodyExtents = ItemModelParts.extents(bodyQuads);
        this.cogQuads = cogQuads;
        this.cogExtents = ItemModelParts.extents(cogQuads);
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext transformType, ClientLevel level, ItemOwner owner, int seed) {
        state.appendModelIdentityElement(this);
        state.setAnimated();
        var body = ItemModelRenderHelper.submitQuads(state, settings, transformType, bodyQuads);
        body.setExtents(bodyExtents);

        LocalPlayer player = Minecraft.getInstance().player;
        float worldTime = AnimationTickHolder.getRenderTime() / 10;
        boolean using = player != null && player.isUsingItem() && player.getItemInHand(player.getUsedItemHand()) == stack;
        float angle = (worldTime * (using ? -200 : -25)) % 360;

        var cog = ItemModelRenderHelper.submitQuads(state, settings, transformType, cogQuads);
        cog.setExtents(cogExtents);
        cog.setLocalTransform(new Matrix4f()
                .translate(0.5f, 0.5f, 0.5f)
                .rotateZ(angle * ((float) Math.PI / 180))
                .translate(0.5f, 0.5f, 0.53125f)
                .translate(-0.5f, -0.5f, -0.5f));
    }

    public record Unbaked(Identifier body) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked(CreateDieselGenerators.rl("item/chemical_sprayer")));
        public static final MapCodec<Unbaked> LIGHTER_CODEC = MapCodec.unit(new Unbaked(CreateDieselGenerators.rl("item/chemical_sprayer_lighter")));

        @Override
        public MapCodec<Unbaked> type() {
            return body.getPath().endsWith("_lighter") ? LIGHTER_CODEC : CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(body);
            resolver.markDependency(COG_ID);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transform) {
            var baker = context.blockModelBaker();
            var parts = ItemModelParts.of(baker, body);
            return new ChemicalSprayerModel(parts.quads(), parts.properties(), ItemModelParts.quads(baker, COG_ID));
        }
    }
}
