package com.jesz.createdieselgenerators.client;

import com.jesz.createdieselgenerators.registrate.ClientHookSink;
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.fn.BlockEntityVisualFactory;
import com.jesz.createdieselgenerators.registrate.fn.NonNullFunction;
import com.zurrtum.create.client.AllBlockEntityRenders;
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class CDGRegistrateClient implements ClientHookSink {
    private CDGRegistrateClient() {
    }

    public static void flush() {
        CDGRegistrateClient sink = new CDGRegistrateClient();
        for (Consumer<ClientHookSink> hook : Registrate.drainClientHooks())
            hook.accept(sink);
    }

    @Override
    public <T extends BlockEntity> void renderer(BlockEntityType<T> type, Supplier<?> renderer) {
        AllBlockEntityRenders.render(type, provider(renderer));
    }

    @Override
    public <T extends BlockEntity> void visual(BlockEntityType<T> type, Supplier<?> renderer, Supplier<?> visual, Predicate<T> renderNormally) {
        SimpleBlockEntityVisualizer.Factory<T> factory = visualizer(visual);
        Predicate<T> skipVanillaRender = entity -> !renderNormally.test(entity);
        if (renderer == null) {
            new SimpleBlockEntityVisualizer.Builder<>(type).factory(factory).skipVanillaRender(skipVanillaRender).apply();
            return;
        }
        AllBlockEntityRenders.visual(type, provider(renderer), factory, skipVanillaRender);
    }



    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityRendererProvider<T, ?> provider(Supplier<?> renderer) {
        NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<T, ?>> function =
                (NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<T, ?>>) renderer.get();
        return context -> castRenderer(function.apply(context));
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity, S extends BlockEntityRenderState> BlockEntityRenderer<T, S> castRenderer(BlockEntityRenderer<T, ?> renderer) {
        return (BlockEntityRenderer<T, S>) renderer;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> SimpleBlockEntityVisualizer.Factory<T> visualizer(Supplier<?> visual) {
        BlockEntityVisualFactory<T> recorded = (BlockEntityVisualFactory<T>) visual.get();
        return recorded::create;
    }
}
