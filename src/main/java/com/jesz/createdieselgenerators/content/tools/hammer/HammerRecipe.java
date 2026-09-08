package com.jesz.createdieselgenerators.content.tools.hammer;

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

public record HammerRecipe(Ingredient ingredient, List<ProcessingOutput> results) implements CreateSingleStackRollableRecipe {

    public static final MapCodec<HammerRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(HammerRecipe::ingredient),
        ProcessingOutput.CODEC.listOf(1, 1).fieldOf("results").forGetter(HammerRecipe::results)
    ).apply(instance, HammerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HammerRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        HammerRecipe::ingredient,
        ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
        HammerRecipe::results,
        HammerRecipe::new
    );

    public static final RecipeSerializer<HammerRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeSerializer<HammerRecipe> getSerializer() {
        return CDGRecipes.HAMMERING.getSerializer();
    }

    @Override
    public RecipeType<HammerRecipe> getType() {
        return CDGRecipes.HAMMERING.getType();
    }
}
