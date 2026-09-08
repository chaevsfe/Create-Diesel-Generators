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

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.data.DataGenContext;
import com.jesz.createdieselgenerators.registrate.data.RegistrateBlockstateProvider;
import com.jesz.createdieselgenerators.registrate.entry.BlockEntry;
import com.jesz.createdieselgenerators.registrate.fn.NonNullBiConsumer;
import com.jesz.createdieselgenerators.registrate.fn.NonNullBiFunction;
import com.jesz.createdieselgenerators.registrate.fn.NonNullFunction;
import com.jesz.createdieselgenerators.registrate.fn.NonNullSupplier;
import com.jesz.createdieselgenerators.registrate.fn.NonNullUnaryOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBuilder<T extends Block, P> extends AbstractBuilder<T, P, BlockBuilder<T, P>> {
    private final NonNullFunction<BlockBehaviour.Properties, T> factory;
    private final List<NonNullUnaryOperator<BlockBehaviour.Properties>> propertyOperators = new ArrayList<>();
    private final List<TagKey<Block>> tags = new ArrayList<>();
    private NonNullSupplier<? extends Block> initialProperties;
    private ItemBuilder<?, BlockBuilder<T, P>> itemBuilder;
    private T registered;

    public BlockBuilder(Registrate owner, P parent, String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        super(owner, name, parent);
        this.factory = factory;
    }

    public BlockBuilder<T, P> initialProperties(NonNullSupplier<? extends Block> block) {
        this.initialProperties = block;
        return this;
    }

    public BlockBuilder<T, P> properties(NonNullUnaryOperator<BlockBehaviour.Properties> operator) {
        propertyOperators.add(operator);
        return this;
    }

    public BlockBuilder<T, P> blockstate(NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> provider) {
        return this;
    }

    public BlockBuilder<T, P> defaultLoot() {
        return this;
    }

    @SafeVarargs
    public final BlockBuilder<T, P> tag(TagKey<Block>... values) {
        Collections.addAll(tags, values);
        return this;
    }

    public List<TagKey<Block>> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public ItemBuilder<BlockItem, BlockBuilder<T, P>> item() {
        return item(BlockItem::new);
    }

    public <I extends Item> ItemBuilder<I, BlockBuilder<T, P>> item(NonNullBiFunction<Block, Item.Properties, I> itemFactory) {
        ItemBuilder<I, BlockBuilder<T, P>> builder = new ItemBuilder<>(getOwner(), this, getName(),
                properties -> itemFactory.apply(requireRegistered(), properties), this::requireRegistered);
        this.itemBuilder = builder;
        return builder;
    }

    public BlockBuilder<T, P> simpleItem() {
        return item().build();
    }

    private T requireRegistered() {
        if (registered == null)
            throw new IllegalStateException("Block " + getId() + " is not registered yet");
        return registered;
    }

    public BlockEntry<T> register() {
        Identifier id = getId();
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        BlockBehaviour.Properties properties = initialProperties == null
                ? BlockBehaviour.Properties.of()
                : BlockBehaviour.Properties.ofFullCopy(initialProperties.get());
        for (NonNullUnaryOperator<BlockBehaviour.Properties> operator : propertyOperators)
            properties = operator.apply(properties);
        T block = factory.apply(properties.setId(key));
        Registry.register(BuiltInRegistries.BLOCK, key, block);
        registered = block;
        BlockEntry<T> entry = new BlockEntry<>(id, block);
        getOwner().track(Registries.BLOCK, entry);
        if (itemBuilder != null)
            itemBuilder.register();
        runRegisterCallbacks(block);
        queueAfterRegisterCallbacks(block);
        return entry;
    }
}
