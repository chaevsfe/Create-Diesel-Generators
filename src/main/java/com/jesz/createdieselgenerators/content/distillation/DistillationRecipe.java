package com.jesz.createdieselgenerators.content.distillation;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.recipe.CDGProcessingRecipe;
import com.jesz.createdieselgenerators.recipe.CDGRecipeParams;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class DistillationRecipe extends CDGProcessingRecipe<RecipeInput> {

    public static final MapCodec<DistillationRecipe> MAP_CODEC = CDGRecipeParams.mapCodec(DistillationRecipe::new, CDGProcessingRecipe::params);
    public static final StreamCodec<RegistryFriendlyByteBuf, DistillationRecipe> STREAM_CODEC = CDGRecipeParams.streamCodec(DistillationRecipe::new, CDGProcessingRecipe::params);
    public static final RecipeSerializer<DistillationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public DistillationRecipe(CDGRecipeParams params) {
        super(params);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public RecipeSerializer<DistillationRecipe> getSerializer() {
        return CDGRecipes.DISTILLATION.getSerializer();
    }

    @Override
    public RecipeType<DistillationRecipe> getType() {
        return CDGRecipes.DISTILLATION.getType();
    }
}
