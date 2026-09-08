/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 chaevsfe
 *
 * Original registration helpers written for the Create Fly ports. The public
 * API names mirror Registrate's so that upstream call sites port unchanged;
 * no Registrate code is included.
 */
package com.jesz.createdieselgenerators.registrate.data;

import net.minecraft.resources.Identifier;

public final class DataGenContext<R, T extends R> {
    private final Identifier id;
    private final T value;

    public DataGenContext(Identifier id, T value) {
        this.id = id;
        this.value = value;
    }

    public Identifier getId() {
        return id;
    }

    public T get() {
        return value;
    }
}
