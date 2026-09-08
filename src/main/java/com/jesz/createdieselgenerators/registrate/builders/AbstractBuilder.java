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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.fn.NonNullConsumer;
import com.jesz.createdieselgenerators.registrate.fn.NonNullUnaryOperator;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBuilder<V, P, S extends AbstractBuilder<V, P, S>> {
    private final Registrate owner;
    private final String name;
    private final P parent;
    private final List<NonNullConsumer<? super V>> onRegister = new ArrayList<>();
    private final List<NonNullConsumer<? super V>> onRegisterAfter = new ArrayList<>();

    protected AbstractBuilder(Registrate owner, String name, P parent) {
        this.owner = owner;
        this.name = name;
        this.parent = parent;
    }

    public Registrate getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public P getParent() {
        return parent;
    }

    public Identifier getId() {
        return owner.id(name);
    }

    @SuppressWarnings("unchecked")
    protected final S self() {
        return (S) this;
    }

    public S onRegister(NonNullConsumer<? super V> callback) {
        onRegister.add(callback);
        return self();
    }

    public S onRegisterAfter(ResourceKey<? extends Registry<?>> registry, NonNullConsumer<? super V> callback) {
        onRegisterAfter.add(callback);
        return self();
    }

    public S transform(NonNullUnaryOperator<S> operator) {
        return operator.apply(self());
    }

    public S lang(String translation) {
        return self();
    }

    public P build() {
        return parent;
    }

    protected void runRegisterCallbacks(V value) {
        for (NonNullConsumer<? super V> callback : onRegister)
            callback.accept(value);
        onRegister.clear();
    }

    protected void queueAfterRegisterCallbacks(V value) {
        for (NonNullConsumer<? super V> callback : onRegisterAfter)
            owner.queueAfterRegister(() -> callback.accept(value));
        onRegisterAfter.clear();
    }
}
