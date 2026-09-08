package com.jesz.createdieselgenerators.content.basin_lid;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.recipe.CDGProcessingRecipe;
import com.jesz.createdieselgenerators.recipe.CDGRecipeParams;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.content.processing.basin.BasinInput;
import com.zurrtum.create.content.processing.basin.BasinRecipe;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import com.zurrtum.create.foundation.recipe.TimedRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class BasinFermentingRecipe extends CDGProcessingRecipe<BasinInput> implements BasinRecipe, TimedRecipe {

    public static final MapCodec<BasinFermentingRecipe> MAP_CODEC = CDGRecipeParams.mapCodec(BasinFermentingRecipe::new, CDGProcessingRecipe::params);
    public static final StreamCodec<RegistryFriendlyByteBuf, BasinFermentingRecipe> STREAM_CODEC = CDGRecipeParams.streamCodec(BasinFermentingRecipe::new, CDGProcessingRecipe::params);
    public static final RecipeSerializer<BasinFermentingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public BasinFermentingRecipe(CDGRecipeParams params) {
        super(params);
    }

    @Override
    public int time() {
        return getProcessingDuration();
    }

    @Override
    public HeatCondition heat() {
        return getRequiredHeat();
    }

    @Override
    public boolean matches(BasinInput input, Level level) {
        if (!heat().testBlazeBurner(input.heat()))
            return false;
        ServerFilteringBehaviour filter = input.filter();
        if (filter == null)
            return false;
        if (getRollableResults().isEmpty()) {
            if (!filter.test(getFluidResults().getFirst()))
                return false;
        } else if (!filter.test(getRollableResults().getFirst().create()))
            return false;

        List<ItemStack> outputs = BasinRecipe.tryCraft(input, ingredients());
        if (outputs == null)
            return false;
        if (!BasinRecipe.matchFluidIngredient(input, fluidIngredients()))
            return false;
        ProcessingOutput.rollOutput(input.random(), getRollableResults(), outputs::add);
        return input.acceptOutputs(outputs, getFluidResults(), true);
    }

    @Override
    public boolean apply(BasinInput input) {
        if (!heat().testBlazeBurner(input.heat()))
            return false;
        Deque<Runnable> commit = new ArrayDeque<>();
        List<ItemStack> outputs = BasinRecipe.prepareCraft(input, ingredients(), commit);
        if (outputs == null)
            return false;
        if (!BasinRecipe.prepareFluidCraft(input, fluidIngredients(), commit))
            return false;
        ProcessingOutput.rollOutput(input.random(), getRollableResults(), outputs::add);
        if (!input.acceptOutputs(outputs, getFluidResults(), true))
            return false;
        commit.forEach(Runnable::run);
        return input.acceptOutputs(outputs, getFluidResults(), false);
    }

    @Override
    public RecipeSerializer<BasinFermentingRecipe> getSerializer() {
        return CDGRecipes.BASIN_FERMENTING.getSerializer();
    }

    @Override
    public RecipeType<BasinFermentingRecipe> getType() {
        return CDGRecipes.BASIN_FERMENTING.getType();
    }
}
