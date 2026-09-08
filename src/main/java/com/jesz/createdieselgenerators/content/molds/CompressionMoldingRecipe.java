package com.jesz.createdieselgenerators.content.molds;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.recipe.CDGProcessingRecipe;
import com.jesz.createdieselgenerators.recipe.CDGRecipeParams;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.content.processing.basin.BasinInput;
import com.zurrtum.create.content.processing.basin.BasinRecipe;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class CompressionMoldingRecipe extends CDGProcessingRecipe<BasinInput> implements BasinRecipe {

    public static final MapCodec<CompressionMoldingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        CDGRecipeParams.MAP_CODEC.forGetter(CDGProcessingRecipe::params),
        Identifier.CODEC.fieldOf("mold").forGetter(CompressionMoldingRecipe::mold)
    ).apply(instance, CompressionMoldingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompressionMoldingRecipe> STREAM_CODEC = StreamCodec.composite(
        CDGRecipeParams.STREAM_CODEC,
        CDGProcessingRecipe::params,
        Identifier.STREAM_CODEC,
        CompressionMoldingRecipe::mold,
        CompressionMoldingRecipe::new
    );

    public static final RecipeSerializer<CompressionMoldingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Identifier mold;
    public final MoldType moldType;

    public CompressionMoldingRecipe(CDGRecipeParams params, Identifier mold) {
        super(params);
        this.mold = mold;
        this.moldType = MoldType.findById(mold);
    }

    public Identifier mold() {
        return mold;
    }

    @Override
    public HeatCondition heat() {
        return getRequiredHeat();
    }

    private boolean hasMold(BasinInput input) {
        if (moldType == null)
            return false;
        Container items = input.items();
        for (int slot = 0; slot < items.getContainerSize(); slot++) {
            ItemStack stack = items.getItem(slot);
            if (stack.getItem() instanceof MoldItem && MoldItem.getMold(stack) == moldType)
                return true;
        }
        return false;
    }

    @Override
    public boolean matches(BasinInput input, Level level) {
        if (!hasMold(input))
            return false;
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
        if (!hasMold(input))
            return false;
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
    public RecipeSerializer<CompressionMoldingRecipe> getSerializer() {
        return CDGRecipes.COMPRESSION_MOLDING.getSerializer();
    }

    @Override
    public RecipeType<CompressionMoldingRecipe> getType() {
        return CDGRecipes.COMPRESSION_MOLDING.getType();
    }
}
