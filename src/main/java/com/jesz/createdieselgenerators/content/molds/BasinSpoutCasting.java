package com.jesz.createdieselgenerators.content.molds;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.zurrtum.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.zurrtum.create.content.fluids.spout.SpoutBlockEntity;
import com.zurrtum.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import com.zurrtum.create.foundation.recipe.RecipeFinder;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import java.util.List;

public class BasinSpoutCasting implements BlockSpoutingBehaviour {
    private static final Object CASTING_RECIPES_KEY = new Object();

    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
        BasinBlockEntity basin;
        if (level.getBlockEntity(pos) instanceof BasinBlockEntity be)
            basin = be;
        else
            return 0;

        if (!(level instanceof ServerLevel serverLevel))
            return 0;

        List<RecipeHolder<?>> recipes = RecipeFinder.get(
                CASTING_RECIPES_KEY, serverLevel,
                holder -> holder.value() instanceof CastingRecipe casting && casting.getType() == CDGRecipes.CASTING.getType());

        for (RecipeHolder<?> holder : recipes) {
            CastingRecipe recipe = (CastingRecipe) holder.value();
            if (!recipe.matches(basin, availableFluid))
                continue;
            if (recipe.getFluidIngredients().getFirst().amount() > availableFluid.getAmount())
                continue;
            int drained = recipe.execute(basin, simulate);
            if (drained > 0)
                return drained;
        }

        return 0;
    }
}
