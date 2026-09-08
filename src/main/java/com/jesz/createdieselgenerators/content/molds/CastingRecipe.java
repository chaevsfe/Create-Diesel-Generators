package com.jesz.createdieselgenerators.content.molds;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.recipe.CDGProcessingRecipe;
import com.jesz.createdieselgenerators.recipe.CDGRecipeParams;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.content.processing.basin.BasinBlockEntity;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CastingRecipe extends CDGProcessingRecipe<RecipeInput> {

    public static final MapCodec<CastingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        CDGRecipeParams.MAP_CODEC.forGetter(CDGProcessingRecipe::params),
        Identifier.CODEC.fieldOf("mold").forGetter(CastingRecipe::mold)
    ).apply(instance, CastingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> STREAM_CODEC = StreamCodec.composite(
        CDGRecipeParams.STREAM_CODEC,
        CDGProcessingRecipe::params,
        Identifier.STREAM_CODEC,
        CastingRecipe::mold,
        CastingRecipe::new
    );

    public static final RecipeSerializer<CastingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Identifier mold;
    public final MoldType moldType;

    public CastingRecipe(CDGRecipeParams params, Identifier mold) {
        super(params);
        this.mold = mold;
        this.moldType = MoldType.findById(mold);
    }

    public Identifier mold() {
        return mold;
    }

    private boolean hasMold(BasinBlockEntity basin) {
        if (moldType == null)
            return false;
        Container items = basin.itemCapability;
        for (int slot = 0; slot < items.getContainerSize(); slot++) {
            ItemStack stack = items.getItem(slot);
            if (stack.getItem() instanceof MoldItem && MoldItem.getMold(stack) == moldType)
                return true;
        }
        return false;
    }

    public boolean matches(BasinBlockEntity basin, FluidStack fluidStack) {
        if (getFluidIngredients().size() != 1)
            return false;
        if (!hasMold(basin))
            return false;
        return getFluidIngredients().getFirst().test(fluidStack);
    }

    public int execute(BasinBlockEntity basin, boolean simulate) {
        if (!hasMold(basin))
            return 0;

        List<ItemStack> recipeOutputItems = new ArrayList<>();
        if (!simulate)
            recipeOutputItems.addAll(rollResults(basin.getLevel().getRandom()));

        if (!basin.acceptOutputs(recipeOutputItems, List.of(), false))
            return 0;

        return getFluidIngredients().getFirst().amount();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public RecipeSerializer<CastingRecipe> getSerializer() {
        return CDGRecipes.CASTING.getSerializer();
    }

    @Override
    public RecipeType<CastingRecipe> getType() {
        return CDGRecipes.CASTING.getType();
    }
}
