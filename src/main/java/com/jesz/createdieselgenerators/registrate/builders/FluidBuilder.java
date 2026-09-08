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

import com.jesz.createdieselgenerators.content.fluids.CDGFluid;
import com.jesz.createdieselgenerators.content.fluids.CDGFluidHolder;
import com.jesz.createdieselgenerators.content.fluids.CDGFluidProperties;
import com.jesz.createdieselgenerators.content.fluids.CDGFluidTypeProperties;
import com.jesz.createdieselgenerators.registrate.Registrate;
import com.jesz.createdieselgenerators.registrate.entry.BlockEntry;
import com.jesz.createdieselgenerators.registrate.entry.FluidEntry;
import com.jesz.createdieselgenerators.registrate.entry.ItemEntry;
import com.jesz.createdieselgenerators.registrate.fn.NonNullBiFunction;
import com.jesz.createdieselgenerators.registrate.fn.NonNullConsumer;
import com.jesz.createdieselgenerators.registrate.fn.NonNullFunction;
import com.jesz.createdieselgenerators.registrate.fn.NonNullUnaryOperator;
import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import com.zurrtum.create.infrastructure.fluids.FluidBlock;
import com.zurrtum.create.AllFluidItemInventory;
import com.zurrtum.create.infrastructure.fluids.BucketFluidInventory;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.List;

public class FluidBuilder<T extends FlowableFluid, P> extends AbstractBuilder<T, P, FluidBuilder<T, P>> {
    private final CDGFluidHolder holder = new CDGFluidHolder();
    private final List<NonNullConsumer<? super BucketItem>> bucketCallbacks = new ArrayList<>();
    private NonNullFunction<CDGFluidHolder, ? extends FlowableFluid> sourceFactory = CDGFluid.Still::new;
    private NonNullFunction<CDGFluidHolder, ? extends FlowableFluid> flowingFactory = CDGFluid.Flowing::new;
    private NonNullBiFunction<Fluid, Item.Properties, ? extends BucketItem> bucketFactory = BucketItem::new;
    private boolean wantsBlock;
    private boolean wantsBucket;

    public FluidBuilder(Registrate owner, P parent, String name, Identifier stillTexture, Identifier flowingTexture) {
        super(owner, name, parent);
        holder.stillTexture = stillTexture;
        holder.flowingTexture = flowingTexture;
    }

    public FluidBuilder<T, P> properties(NonNullUnaryOperator<CDGFluidTypeProperties> operator) {
        operator.apply(holder.typeProperties);
        return this;
    }

    public FluidBuilder<T, P> fluidProperties(NonNullUnaryOperator<CDGFluidProperties> operator) {
        operator.apply(holder.properties);
        return this;
    }

    public FluidBuilder<T, P> source(NonNullFunction<CDGFluidHolder, ? extends FlowableFluid> factory) {
        this.sourceFactory = factory;
        return this;
    }

    public FluidBuilder<T, P> flowing(NonNullFunction<CDGFluidHolder, ? extends FlowableFluid> factory) {
        this.flowingFactory = factory;
        return this;
    }

    public BlockSubBuilder block() {
        wantsBlock = true;
        return new BlockSubBuilder();
    }

    public BucketSubBuilder bucket() {
        wantsBucket = true;
        return new BucketSubBuilder();
    }

    public BucketSubBuilder bucket(NonNullBiFunction<Fluid, Item.Properties, ? extends BucketItem> factory) {
        wantsBucket = true;
        this.bucketFactory = factory;
        return new BucketSubBuilder();
    }

    public class BlockSubBuilder {
        public FluidBuilder<T, P> build() {
            return FluidBuilder.this;
        }
    }

    public class BucketSubBuilder {
        public BucketSubBuilder onRegister(NonNullConsumer<? super BucketItem> callback) {
            bucketCallbacks.add(callback);
            return this;
        }

        public FluidBuilder<T, P> build() {
            return FluidBuilder.this;
        }
    }

    @SuppressWarnings("unchecked")
    public FluidEntry<T> register() {
        Identifier id = getId();
        Identifier flowingId = getOwner().id("flowing_" + getName());

        holder.still = sourceFactory.apply(holder);
        holder.flowing = flowingFactory.apply(holder);
        Registry.register(BuiltInRegistries.FLUID, id, holder.still);
        Registry.register(BuiltInRegistries.FLUID, flowingId, holder.flowing);

        BlockEntry<Block> blockEntry = wantsBlock ? new BlockEntry<>(id, null) : null;
        ItemEntry<BucketItem> bucketEntry = wantsBucket ? new ItemEntry<>(getOwner().id(getName() + "_bucket"), null) : null;
        FluidEntry<T> entry = new FluidEntry<>(id, holder, blockEntry, bucketEntry);

        if (wantsBlock)
            Registrate.queueFluidBlock(() -> {
                ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
                BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WATER)
                    .replaceable()
                    .noCollision()
                    .strength(100.0F)
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable()
                    .liquid()
                    .sound(SoundType.EMPTY)
                    .setId(blockKey);
                holder.block = new FluidBlock(holder.still, properties);
                Registry.register(BuiltInRegistries.BLOCK, blockKey, holder.block);
                blockEntry.setValue(holder.block);
                getOwner().track(Registries.BLOCK, blockEntry);
            });

        if (wantsBucket)
            Registrate.queueFluidItem(() -> {
                Identifier bucketId = getOwner().id(getName() + "_bucket");
                ResourceKey<Item> bucketKey = ResourceKey.create(Registries.ITEM, bucketId);
                holder.bucket = bucketFactory.apply(
                    holder.still,
                    new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1).setId(bucketKey));
                Registry.register(BuiltInRegistries.ITEM, bucketKey, holder.bucket);
                bucketEntry.setValue(holder.bucket);
                getOwner().track(Registries.ITEM, bucketEntry);
                AllFluidItemInventory.ALL.put(holder.bucket, new AllFluidItemInventory.Entry(BucketFluidInventory::new));
                for (NonNullConsumer<? super BucketItem> callback : bucketCallbacks)
                    callback.accept(holder.bucket);
                bucketCallbacks.clear();
            });

        getOwner().track(Registries.FLUID, entry);
        runRegisterCallbacks((T) holder.flowing);
        queueAfterRegisterCallbacks((T) holder.flowing);
        return entry;
    }
}
