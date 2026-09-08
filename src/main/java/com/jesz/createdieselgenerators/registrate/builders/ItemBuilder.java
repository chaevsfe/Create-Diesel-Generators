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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.data.DataGenContext;
import com.jesz.createdieselgenerators.registrate.data.RegistrateItemModelProvider;
import com.jesz.createdieselgenerators.registrate.entry.ItemEntry;
import com.jesz.createdieselgenerators.registrate.fn.NonNullBiConsumer;
import com.jesz.createdieselgenerators.registrate.fn.NonNullFunction;
import com.jesz.createdieselgenerators.registrate.fn.NonNullSupplier;
import com.jesz.createdieselgenerators.registrate.fn.NonNullUnaryOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemBuilder<T extends Item, P> extends AbstractBuilder<T, P, ItemBuilder<T, P>> {
    private final NonNullFunction<Item.Properties, T> factory;
    private final NonNullSupplier<Block> owningBlock;
    private final List<NonNullUnaryOperator<Item.Properties>> propertyOperators = new ArrayList<>();
    private final List<TagKey<Item>> tags = new ArrayList<>();

    public ItemBuilder(Registrate owner, P parent, String name, NonNullFunction<Item.Properties, T> factory) {
        this(owner, parent, name, factory, null);
    }

    public ItemBuilder(Registrate owner, P parent, String name, NonNullFunction<Item.Properties, T> factory, NonNullSupplier<Block> owningBlock) {
        super(owner, name, parent);
        this.factory = factory;
        this.owningBlock = owningBlock;
    }

    public ItemBuilder<T, P> properties(NonNullUnaryOperator<Item.Properties> operator) {
        propertyOperators.add(operator);
        return this;
    }

    public ItemBuilder<T, P> model(NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> provider) {
        return this;
    }

    public ItemBuilder<T, P> defaultModel() {
        return this;
    }

    @SafeVarargs
    public final ItemBuilder<T, P> tag(TagKey<Item>... values) {
        Collections.addAll(tags, values);
        return this;
    }

    public List<TagKey<Item>> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public ItemEntry<T> register() {
        Identifier id = getId();
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Item.Properties properties = new Item.Properties();
        if (owningBlock != null)
            properties = properties.useBlockDescriptionPrefix();
        for (NonNullUnaryOperator<Item.Properties> operator : propertyOperators)
            properties = operator.apply(properties);
        T item = factory.apply(properties.setId(key));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        if (owningBlock != null)
            Item.BY_BLOCK.put(owningBlock.get(), item);
        ItemEntry<T> entry = new ItemEntry<>(id, item);
        getOwner().track(Registries.ITEM, entry);
        getOwner().registerTooltipModifier(item);
        runRegisterCallbacks(item);
        queueAfterRegisterCallbacks(item);
        return entry;
    }
}
