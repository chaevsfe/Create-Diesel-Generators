/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 chaevsfe
 */
package com.jesz.createdieselgenerators.registrate.data;

import com.jesz.createdieselgenerators.registrate.builders.BlockBuilder;
import com.jesz.createdieselgenerators.registrate.fn.NonNullUnaryOperator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class TagGen {
    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> axeOrPickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE).tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> axeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> pickaxeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
}
