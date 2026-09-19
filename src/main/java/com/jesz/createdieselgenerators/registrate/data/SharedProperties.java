/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 chaevsfe
 */
package com.jesz.createdieselgenerators.registrate.data;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SharedProperties {
    public static Block wooden() {
        return Blocks.STRIPPED_SPRUCE_WOOD;
    }

    public static Block stone() {
        return Blocks.ANDESITE;
    }

    public static Block softMetal() {
        return Blocks.GOLD_BLOCK;
    }

    public static Block copperMetal() {
        return Blocks.COPPER_BLOCK;
    }

    public static Block netheriteMetal() {
        return Blocks.NETHERITE_BLOCK;
    }
}
