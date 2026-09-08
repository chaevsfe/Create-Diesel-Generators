package com.jesz.createdieselgenerators.content.tools.wire_cutters;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.foundation.recipe.CreateSingleStackRollableRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public record WireCuttingRecipe(Ingredient ingredient, List<ProcessingOutput> results) implements CreateSingleStackRollableRecipe {

    public static final MapCodec<WireCuttingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(WireCuttingRecipe::ingredient),
        ProcessingOutput.CODEC.listOf(1, 1).fieldOf("results").forGetter(WireCuttingRecipe::results)
    ).apply(instance, WireCuttingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WireCuttingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        WireCuttingRecipe::ingredient,
        ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
        WireCuttingRecipe::results,
        WireCuttingRecipe::new
    );

    public static final RecipeSerializer<WireCuttingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeSerializer<WireCuttingRecipe> getSerializer() {
        return CDGRecipes.WIRE_CUTTING.getSerializer();
    }

    @Override
    public RecipeType<WireCuttingRecipe> getType() {
        return CDGRecipes.WIRE_CUTTING.getType();
    }
}
