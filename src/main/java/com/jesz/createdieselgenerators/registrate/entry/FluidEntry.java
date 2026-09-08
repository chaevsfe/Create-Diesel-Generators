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

import com.jesz.createdieselgenerators.content.fluids.CDGFluidHolder;
import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings("unchecked")
public class FluidEntry<T extends FlowableFluid> extends RegistryEntry<Fluid, T> {
    private final CDGFluidHolder holder;
    private final BlockEntry<Block> block;
    private final ItemEntry<BucketItem> bucket;

    public FluidEntry(Identifier id, CDGFluidHolder holder, BlockEntry<Block> block, ItemEntry<BucketItem> bucket) {
        super(id, (T) holder.flowing);
        this.holder = holder;
        this.block = block;
        this.bucket = bucket;
    }

    public CDGFluidHolder getHolder() {
        return holder;
    }

    public FlowableFluid getSource() {
        return holder.still;
    }

    public BlockEntry<Block> getBlock() {
        return block;
    }

    public ItemEntry<BucketItem> getBucket() {
        return bucket;
    }
}
