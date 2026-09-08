package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.content.basin_lid.BasinFermentingRecipe;
import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermentingRecipe;
import com.jesz.createdieselgenerators.content.distillation.DistillationRecipe;
import com.jesz.createdieselgenerators.content.molds.CastingRecipe;
import com.jesz.createdieselgenerators.content.molds.CompressionMoldingRecipe;
import com.jesz.createdieselgenerators.content.tools.hammer.HammerRecipe;
import com.jesz.createdieselgenerators.content.tools.wire_cutters.WireCuttingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CDGRecipes {

    public static final Entry<BasinFermentingRecipe> BASIN_FERMENTING = register("basin_fermenting", BasinFermentingRecipe.SERIALIZER);
    public static final Entry<BulkFermentingRecipe> BULK_FERMENTING = register("bulk_fermenting", BulkFermentingRecipe.SERIALIZER);
    public static final Entry<DistillationRecipe> DISTILLATION = register("distillation", DistillationRecipe.SERIALIZER);
    public static final Entry<CompressionMoldingRecipe> COMPRESSION_MOLDING = register("compression_molding", CompressionMoldingRecipe.SERIALIZER);
    public static final Entry<CastingRecipe> CASTING = register("casting", CastingRecipe.SERIALIZER);
    public static final Entry<WireCuttingRecipe> WIRE_CUTTING = register("wire_cutting", WireCuttingRecipe.SERIALIZER);
    public static final Entry<HammerRecipe> HAMMERING = register("hammering", HammerRecipe.SERIALIZER);

    private static <T extends Recipe<?>> Entry<T> register(String name, RecipeSerializer<T> serializer) {
        Identifier id = CreateDieselGenerators.rl(name);
        RecipeType<T> type = Registry.register(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });
        return new Entry<>(id, type, Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer));
    }

    public static void register() {
    }

    public record Entry<T extends Recipe<?>>(Identifier id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        public Identifier getId() {
            return id;
        }

        public RecipeType<T> getType() {
            return type;
        }

        public RecipeSerializer<T> getSerializer() {
            return serializer;
        }
    }
}
