package com.jesz.createdieselgenerators.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jesz.createdieselgenerators.CDGDataComponents;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.tools.lighter.LighterState;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LighterItemModel implements ItemModel {
    public static final Identifier ID = CreateDieselGenerators.rl("model/lighter");
    public static final String STANDARD = "standard";

    private static final Map<String, String> SKIN_IDS = new HashMap<>();

    private final Map<String, List<BakedQuad>> quads;
    private final Map<String, Supplier<Vector3fc[]>> extents = new HashMap<>();
    private final ModelRenderProperties settings;

    public LighterItemModel(Map<String, List<BakedQuad>> quads, ModelRenderProperties settings) {
        this.quads = quads;
        this.settings = settings;
        quads.forEach((key, list) -> extents.put(key, ItemModelParts.extents(list)));
    }

    public static Map<String, String> skinIds() {
        return SKIN_IDS;
    }

    private static String suffix(LighterState state) {
        return switch (state) {
            case OPEN -> "_open";
            case OPEN_IGNITED -> "_ignited";
            default -> "";
        };
    }

    private static Identifier modelId(String skin, LighterState state) {
        if (STANDARD.equals(skin))
            return CreateDieselGenerators.rl("item/lighter" + suffix(state));
        return CreateDieselGenerators.rl("item/lighter/" + skin + suffix(state));
    }

    private static String key(String skin, LighterState state) {
        return skin + suffix(state);
    }

    static void reloadSkinIds() {
        SKIN_IDS.clear();
        var manager = Minecraft.getInstance().getResourceManager();
        for (String namespace : manager.getNamespaces()) {
            Optional<Resource> resource = manager.getResource(Identifier.fromNamespaceAndPath(namespace, "lighter_skins.json"));
            if (resource.isEmpty())
                continue;
            try (var reader = resource.get().openAsReader()) {
                JsonElement data = JsonParser.parseReader(reader);
                for (JsonElement element : data.getAsJsonArray())
                    SKIN_IDS.put(element.getAsJsonObject().getAsJsonPrimitive("name").getAsString(),
                            element.getAsJsonObject().getAsJsonPrimitive("id").getAsString());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext transformType, ClientLevel level, ItemOwner owner, int seed) {
        state.appendModelIdentityElement(this);
        LighterState lighterState = stack.getOrDefault(CDGDataComponents.LIGHTER_STATE, LighterState.CLOSED);
        String skin = SKIN_IDS.get(stack.getHoverName().getString().toLowerCase(Locale.ROOT));
        String key = key(skin == null ? STANDARD : skin, lighterState);
        if (!quads.containsKey(key))
            key = key(STANDARD, lighterState);
        state.appendModelIdentityElement(key);
        var layer = ItemModelRenderHelper.submitQuads(state, settings, transformType, quads.get(key));
        layer.setExtents(extents.get(key));
    }

    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            reloadSkinIds();
            for (LighterState state : LighterState.values()) {
                resolver.markDependency(modelId(STANDARD, state));
                for (String skin : SKIN_IDS.values())
                    resolver.markDependency(modelId(skin, state));
            }
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transform) {
            var baker = context.blockModelBaker();
            Map<String, List<BakedQuad>> byKey = new HashMap<>();
            var standard = ItemModelParts.of(baker, modelId(STANDARD, LighterState.CLOSED));
            byKey.put(key(STANDARD, LighterState.CLOSED), standard.quads());
            for (LighterState state : LighterState.values()) {
                if (state != LighterState.CLOSED)
                    byKey.put(key(STANDARD, state), ItemModelParts.quads(baker, modelId(STANDARD, state)));
                for (String skin : SKIN_IDS.values())
                    byKey.put(key(skin, state), ItemModelParts.quads(baker, modelId(skin, state)));
            }
            return new LighterItemModel(byKey, standard.properties());
        }
    }
}
