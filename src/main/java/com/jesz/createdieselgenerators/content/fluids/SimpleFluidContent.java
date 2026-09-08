package com.jesz.createdieselgenerators.content.fluids;

import com.mojang.serialization.Codec;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import com.jesz.createdieselgenerators.content.fluids.SimpleFluidContent;

public record SimpleFluidContent(Holder<Fluid> fluid, int amount, DataComponentPatch components) {

    public static final SimpleFluidContent EMPTY =
        new SimpleFluidContent(BuiltInRegistries.FLUID.wrapAsHolder(Fluids.EMPTY), 0, DataComponentPatch.EMPTY);

    public static final Codec<SimpleFluidContent> CODEC =
        FluidStack.OPTIONAL_CODEC.xmap(SimpleFluidContent::copyOf, SimpleFluidContent::copy);

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleFluidContent> STREAM_CODEC =
        FluidStack.OPTIONAL_PACKET_CODEC.map(SimpleFluidContent::copyOf, SimpleFluidContent::copy);

    public static SimpleFluidContent copyOf(FluidStack stack) {
        return stack.isEmpty() ? EMPTY : new SimpleFluidContent(stack.getRegistryEntry(), stack.getAmount(), stack.getComponentChanges());
    }

    public FluidStack copy() {
        return isEmpty() ? FluidStack.EMPTY : new FluidStack(fluid, amount, components);
    }

    public boolean isEmpty() {
        return amount <= 0;
    }
}
