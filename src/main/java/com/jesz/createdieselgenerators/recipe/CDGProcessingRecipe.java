package com.jesz.createdieselgenerators.recipe;

import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import com.zurrtum.create.foundation.recipe.CreateRecipe;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public abstract class CDGProcessingRecipe<T extends RecipeInput> implements CreateRecipe<T> {

    protected final CDGRecipeParams params;

    protected CDGProcessingRecipe(CDGRecipeParams params) {
        this.params = params;
    }

    public CDGRecipeParams params() {
        return params;
    }

    public List<SizedIngredient> ingredients() {
        return params.ingredients();
    }

    public List<FluidIngredient> fluidIngredients() {
        return params.fluidIngredients();
    }

    public List<SizedIngredient> getIngredients() {
        return params.ingredients();
    }

    public List<FluidIngredient> getFluidIngredients() {
        return params.fluidIngredients();
    }

    public List<ProcessingOutput> getRollableResults() {
        return params.results();
    }

    public List<FluidStack> getFluidResults() {
        return params.fluidResults();
    }

    public int getProcessingDuration() {
        return params.processingDuration();
    }

    public HeatCondition getRequiredHeat() {
        return params.requiredHeat();
    }

    public int getIngredientSize() {
        return params.ingredients().size() + params.fluidIngredients().size();
    }

    public List<ItemStack> rollResults(RandomSource random) {
        List<ItemStack> rolled = new ArrayList<>();
        ProcessingOutput.rollOutput(random, params.results(), rolled::add);
        return rolled;
    }

    public ItemStack getResultItem() {
        List<ProcessingOutput> results = params.results();
        return results.isEmpty() ? ItemStack.EMPTY : results.getFirst().create();
    }

    @Override
    public ItemStack assemble(T input) {
        return getResultItem();
    }
}
