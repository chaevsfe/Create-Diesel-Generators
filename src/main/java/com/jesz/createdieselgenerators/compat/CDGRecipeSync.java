package com.jesz.createdieselgenerators.compat;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.zurrtum.create.compat.Mods;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public final class CDGRecipeSync {
    private CDGRecipeSync() {
    }

    public static List<RecipeSerializer<?>> serializers() {
        return List.of(
            CDGRecipes.BASIN_FERMENTING.getSerializer(),
            CDGRecipes.BULK_FERMENTING.getSerializer(),
            CDGRecipes.DISTILLATION.getSerializer(),
            CDGRecipes.COMPRESSION_MOLDING.getSerializer(),
            CDGRecipes.CASTING.getSerializer(),
            CDGRecipes.WIRE_CUTTING.getSerializer(),
            CDGRecipes.HAMMERING.getSerializer()
        );
    }

    public static void register() {
        if (!Mods.JEI.isLoaded() && !Mods.RRV.isLoaded())
            return;
        for (RecipeSerializer<?> serializer : serializers())
            RecipeSynchronization.synchronizeRecipeSerializer(serializer);
    }
}
