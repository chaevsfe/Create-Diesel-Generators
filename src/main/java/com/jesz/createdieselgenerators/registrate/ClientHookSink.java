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

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ClientHookSink {
    <T extends BlockEntity> void renderer(BlockEntityType<T> type, Supplier<?> renderer);

    <T extends BlockEntity> void visual(BlockEntityType<T> type, Supplier<?> renderer, Supplier<?> visual, Predicate<T> renderNormally);
}
