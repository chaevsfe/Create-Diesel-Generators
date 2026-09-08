package com.jesz.createdieselgenerators.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Function;

public record CDGRecipeParams(
    List<SizedIngredient> ingredients,
    List<FluidIngredient> fluidIngredients,
    List<ProcessingOutput> results,
    List<FluidStack> fluidResults,
    int processingDuration,
    HeatCondition requiredHeat
) {
    public static final MapCodec<CDGRecipeParams> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SizedIngredient.LIST_CODEC.optionalFieldOf("ingredients", List.of()).forGetter(CDGRecipeParams::ingredients),
        FluidIngredient.CODEC.listOf().optionalFieldOf("fluid_ingredients", List.of()).forGetter(CDGRecipeParams::fluidIngredients),
        ProcessingOutput.CODEC.listOf().optionalFieldOf("results", List.of()).forGetter(CDGRecipeParams::results),
        FluidStack.CODEC.listOf().optionalFieldOf("fluid_results", List.of()).forGetter(CDGRecipeParams::fluidResults),
        Codec.INT.optionalFieldOf("processing_time", 0).forGetter(CDGRecipeParams::processingDuration),
        HeatCondition.CODEC.optionalFieldOf("heat_requirement", HeatCondition.NONE).forGetter(CDGRecipeParams::requiredHeat)
    ).apply(instance, CDGRecipeParams::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CDGRecipeParams> STREAM_CODEC = StreamCodec.of(
        (buffer, params) -> {
            SizedIngredient.PACKET_CODEC.apply(ByteBufCodecs.list()).encode(buffer, params.ingredients);
            FluidIngredient.PACKET_CODEC.apply(ByteBufCodecs.list()).encode(buffer, params.fluidIngredients);
            ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, params.results);
            FluidStack.PACKET_CODEC.apply(ByteBufCodecs.list()).encode(buffer, params.fluidResults);
            ByteBufCodecs.VAR_INT.encode(buffer, params.processingDuration);
            HeatCondition.PACKET_CODEC.encode(buffer, params.requiredHeat);
        },
        buffer -> new CDGRecipeParams(
            SizedIngredient.PACKET_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
            FluidIngredient.PACKET_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
            ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
            FluidStack.PACKET_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
            ByteBufCodecs.VAR_INT.decode(buffer),
            HeatCondition.PACKET_CODEC.decode(buffer)
        )
    );

    public static <R> MapCodec<R> mapCodec(Function<CDGRecipeParams, R> factory, Function<R, CDGRecipeParams> getter) {
        return MAP_CODEC.xmap(factory, getter);
    }

    public static <R> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(Function<CDGRecipeParams, R> factory, Function<R, CDGRecipeParams> getter) {
        return STREAM_CODEC.map(factory, getter);
    }
}
