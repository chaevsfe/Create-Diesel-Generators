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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.entry.EntityEntry;
import com.jesz.createdieselgenerators.registrate.fn.NonNullConsumer;

import java.util.ArrayList;
import java.util.List;

public class EntityBuilder<T extends Entity, P> extends AbstractBuilder<EntityType<T>, P, EntityBuilder<T, P>> {
    private final EntityType.EntityFactory<T> factory;
    private final MobCategory category;
    private final List<NonNullConsumer<EntityType.Builder<T>>> propertyOperators = new ArrayList<>();

    public EntityBuilder(Registrate owner, P parent, String name, EntityType.EntityFactory<T> factory, MobCategory category) {
        super(owner, name, parent);
        this.factory = factory;
        this.category = category;
    }

    public EntityBuilder<T, P> properties(NonNullConsumer<EntityType.Builder<T>> operator) {
        propertyOperators.add(operator);
        return this;
    }

    public EntityEntry<T> register() {
        Identifier id = getId();
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType.Builder<T> builder = EntityType.Builder.of(factory, category);
        for (NonNullConsumer<EntityType.Builder<T>> operator : propertyOperators)
            operator.accept(builder);
        EntityType<T> type = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        EntityEntry<T> entry = new EntityEntry<>(id, type);
        getOwner().track(Registries.ENTITY_TYPE, entry);
        runRegisterCallbacks(type);
        queueAfterRegisterCallbacks(type);
        return entry;
    }
}
