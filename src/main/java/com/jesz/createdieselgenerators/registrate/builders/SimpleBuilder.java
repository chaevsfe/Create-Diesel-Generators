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
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.entry.RegistryEntry;

import java.util.function.Supplier;

public class SimpleBuilder<R, T extends R, P> extends AbstractBuilder<T, P, SimpleBuilder<R, T, P>> {
    private final Registry<R> registry;
    private final Supplier<T> supplier;

    public SimpleBuilder(Registrate owner, P parent, String name, Registry<R> registry, Supplier<T> supplier) {
        super(owner, name, parent);
        this.registry = registry;
        this.supplier = supplier;
    }

    public RegistryEntry<R, T> register() {
        Identifier id = getId();
        T value = Registry.register(registry, id, supplier.get());
        RegistryEntry<R, T> entry = new RegistryEntry<>(id, value);
        runRegisterCallbacks(value);
        queueAfterRegisterCallbacks(value);
        return entry;
    }
}
