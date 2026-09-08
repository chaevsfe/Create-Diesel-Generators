/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 chaevsfe
 *
 * Original registration helpers written for the Create Fly ports. The public
 * API names mirror Registrate's so that upstream call sites port unchanged;
 * no Registrate code is included.
 */
package com.jesz.createdieselgenerators.registrate.entry;

import net.minecraft.resources.Identifier;

import com.jesz.createdieselgenerators.registrate.fn.NonNullSupplier;

public class RegistryEntry<R, T extends R> implements NonNullSupplier<T> {
    private final Identifier id;
    private T value;

    public RegistryEntry(Identifier id, T value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }
}
