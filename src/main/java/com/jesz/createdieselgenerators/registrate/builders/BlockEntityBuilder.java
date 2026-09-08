/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 chaevsfe
 *
 * Original registration helpers written for the Create Fly ports. The public
 * API names mirror Registrate's so that upstream call sites port unchanged;
 * no Registrate code is included.
 */
package com.jesz.createdieselgenerators.registrate.builders;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.jesz.createdieselgenerators.registrate.ClientHookSink;
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.entry.BlockEntityEntry;
import com.jesz.createdieselgenerators.registrate.fn.NonNullFunction;
import com.jesz.createdieselgenerators.registrate.fn.NonNullSupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class BlockEntityBuilder<T extends BlockEntity, P> extends AbstractBuilder<BlockEntityType<T>, P, BlockEntityBuilder<T, P>> {
    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockEntityType<?> type, BlockPos pos, BlockState state);
    }

    private final BlockEntityFactory<T> factory;
    private final List<NonNullSupplier<? extends Block>> validBlocks = new ArrayList<>();
    private Supplier<?> renderer;

    public BlockEntityBuilder(Registrate owner, P parent, String name, BlockEntityFactory<T> factory) {
        super(owner, name, parent);
        this.factory = factory;
    }

    public BlockEntityBuilder<T, P> validBlock(NonNullSupplier<? extends Block> block) {
        validBlocks.add(block);
        return this;
    }

    @SafeVarargs
    public final BlockEntityBuilder<T, P> validBlocks(NonNullSupplier<? extends Block>... blocks) {
        Collections.addAll(validBlocks, blocks);
        return this;
    }

    public BlockEntityBuilder<T, P> renderer(NonNullSupplier<NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T, ?>>> renderer) {
        this.renderer = renderer;
        return this;
    }

    @Nullable
    protected Supplier<?> rendererSupplier() {
        return renderer;
    }

    protected BlockEntityType<T> createEntry() {
        Set<Block> blocks = new LinkedHashSet<>();
        for (NonNullSupplier<? extends Block> block : validBlocks)
            blocks.add(block.get());
        AtomicReference<BlockEntityType<T>> self = new AtomicReference<>();
        BlockEntityType<T> type = new BlockEntityType<>((pos, state) -> factory.create(self.get(), pos, state), blocks);
        self.set(type);
        return type;
    }

    protected void registerClientHooks(BlockEntityType<T> type) {
        Supplier<?> factory = rendererSupplier();
        if (factory == null)
            return;
        Registrate.addClientHook(sink -> sink.renderer(type, factory));
    }

    public BlockEntityEntry<T> register() {
        Identifier id = getId();
        BlockEntityType<T> type = createEntry();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type);
        BlockEntityEntry<T> entry = new BlockEntityEntry<>(id, type);
        getOwner().track(Registries.BLOCK_ENTITY_TYPE, entry);
        runRegisterCallbacks(type);
        queueAfterRegisterCallbacks(type);
        registerClientHooks(type);
        return entry;
    }
}
