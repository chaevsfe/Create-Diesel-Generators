/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 chaevsfe
 *
 * Original registration helpers written for the Create Fly ports. The public
 * API names mirror Registrate's so that upstream call sites port unchanged;
 * no Registrate code is included.
 */
package com.jesz.createdieselgenerators.registrate.fn;

import java.util.function.BiFunction;

@FunctionalInterface
public interface NonNullBiFunction<T, U, R> extends BiFunction<T, U, R> {
}
