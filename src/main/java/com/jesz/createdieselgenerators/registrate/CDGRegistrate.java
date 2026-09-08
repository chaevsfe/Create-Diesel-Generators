/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 chaevsfe
 *
 * Original registration helpers written for the Create Fly ports. The public
 * API names mirror Registrate's so that upstream call sites port unchanged;
 * no Registrate code is included.
 */
package com.jesz.createdieselgenerators.registrate;

import com.jesz.createdieselgenerators.registrate.builders.BlockEntityBuilder;
import com.jesz.createdieselgenerators.registrate.builders.FluidBuilder;
import com.jesz.createdieselgenerators.registrate.builders.SimpleBuilder;
import com.jesz.createdieselgenerators.registrate.entry.BlockEntityEntry;
import com.jesz.createdieselgenerators.registrate.entry.RegistryEntry;
import com.jesz.createdieselgenerators.registrate.fn.BlockEntityVisualFactory;
import com.jesz.createdieselgenerators.registrate.fn.NonNullConsumer;
import com.jesz.createdieselgenerators.registrate.fn.NonNullSupplier;
import com.zurrtum.create.api.behaviour.display.DisplaySource;
import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CDGRegistrate extends Registrate {
    private Function<Item, TooltipModifier> tooltipModifierFactory;
    private final Set<Item> tooltipItems = Collections.newSetFromMap(new IdentityHashMap<>());

    protected CDGRegistrate(String modid) {
        super(modid);
    }

    public static CDGRegistrate create(String modid) {
        return new CDGRegistrate(modid);
    }

    public CDGRegistrate setTooltipModifierFactory(Function<Item, TooltipModifier> factory) {
        this.tooltipModifierFactory = factory;
        getAll(Registries.ITEM).forEach(entry -> registerTooltipModifier(entry.get()));
        return this;
    }

    public CDGRegistrate defaultCreativeTab(ResourceKey<CreativeModeTab> tab) {
        return this;
    }

    @Override
    public void registerTooltipModifier(Item item) {
        if (tooltipModifierFactory == null || !tooltipItems.add(item))
            return;
        TooltipModifier.REGISTRY.register(item, tooltipModifierFactory.apply(item));
    }

    @Override
    public <T extends BlockEntity> CDGBlockEntityBuilder<T, Registrate> blockEntity(String name, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        return new CDGBlockEntityBuilder<>(this, this, name, factory);
    }

    public <T extends FlowableFluid> FluidBuilder<T, CDGRegistrate> fluid(String name, Identifier stillTexture, Identifier flowingTexture) {
        return new FluidBuilder<>(this, this, name, stillTexture, flowingTexture);
    }

    public <T extends DisplaySource> SimpleBuilder<DisplaySource, T, CDGRegistrate> displaySource(String name, Supplier<T> supplier) {
        return new SimpleBuilder<>(this, this, name, CreateRegistries.DISPLAY_SOURCE, supplier);
    }

    public RegistryEntry<SoundEvent, SoundEvent> soundEvent(String name) {
        Identifier id = id(name);
        SoundEvent event = Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
        RegistryEntry<SoundEvent, SoundEvent> entry = new RegistryEntry<>(id, event);
        track(Registries.SOUND_EVENT, entry);
        return entry;
    }

    public static class CDGBlockEntityBuilder<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> {
        private Supplier<?> visualFactory;
        private Predicate<T> renderNormally;
        private final List<NonNullSupplier<? extends Collection<? extends NonNullSupplier<? extends Block>>>> deferredValidBlocks = new ArrayList<>();
        private final List<RegistryEntry<DisplaySource, ? extends DisplaySource>> displaySources = new ArrayList<>();

        protected CDGBlockEntityBuilder(Registrate owner, P parent, String name, BlockEntityFactory<T> factory) {
            super(owner, parent, name, factory);
        }

        public CDGBlockEntityBuilder<T, P> validBlocksDeferred(NonNullSupplier<? extends Collection<? extends NonNullSupplier<? extends Block>>> blocks) {
            deferredValidBlocks.add(blocks);
            return this;
        }

        public CDGBlockEntityBuilder<T, P> displaySource(RegistryEntry<DisplaySource, ? extends DisplaySource> source) {
            displaySources.add(source);
            return this;
        }

        public CDGBlockEntityBuilder<T, P> visual(NonNullSupplier<BlockEntityVisualFactory<T>> visual) {
            return visual(visual, false);
        }

        public CDGBlockEntityBuilder<T, P> visual(NonNullSupplier<BlockEntityVisualFactory<T>> visual, boolean renderNormally) {
            this.visualFactory = visual;
            this.renderNormally = entity -> renderNormally;
            return this;
        }

        public CDGBlockEntityBuilder<T, P> visual(NonNullSupplier<BlockEntityVisualFactory<T>> visual, Predicate<T> renderNormally) {
            this.visualFactory = visual;
            this.renderNormally = renderNormally;
            return this;
        }

        @Override
        protected BlockEntityType<T> createEntry() {
            for (NonNullSupplier<? extends Collection<? extends NonNullSupplier<? extends Block>>> deferred : deferredValidBlocks)
                for (NonNullSupplier<? extends Block> block : deferred.get())
                    validBlock(block);
            return super.createEntry();
        }

        @Override
        public BlockEntityEntry<T> register() {
            BlockEntityEntry<T> entry = super.register();
            for (RegistryEntry<DisplaySource, ? extends DisplaySource> source : displaySources)
                DisplaySource.BY_BLOCK_ENTITY.add(entry.get(), source.get());
            return entry;
        }

        @Override
        protected void registerClientHooks(BlockEntityType<T> type) {
            Supplier<?> renderer = rendererSupplier();
            if (visualFactory == null) {
                super.registerClientHooks(type);
                return;
            }
            Supplier<?> visual = visualFactory;
            Predicate<T> normally = renderNormally == null ? entity -> false : renderNormally;
            Registrate.addClientHook(sink -> sink.visual(type, renderer, visual, normally));
        }
    }

    public static <T extends Entity> EntityType<T> entityType(String modid, String name, EntityType.EntityFactory<T> factory, MobCategory category) {
        throw new UnsupportedOperationException();
    }
}
