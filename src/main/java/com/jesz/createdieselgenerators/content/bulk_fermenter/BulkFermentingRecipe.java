package com.jesz.createdieselgenerators.content.bulk_fermenter;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.foundation.CDGInv;
import com.jesz.createdieselgenerators.recipe.CDGProcessingRecipe;
import com.jesz.createdieselgenerators.recipe.CDGRecipeParams;
import com.mojang.serialization.MapCodec;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BulkFermentingRecipe extends CDGProcessingRecipe<RecipeInput> {

    public static final MapCodec<BulkFermentingRecipe> MAP_CODEC = CDGRecipeParams.mapCodec(BulkFermentingRecipe::new, CDGProcessingRecipe::params);
    public static final StreamCodec<RegistryFriendlyByteBuf, BulkFermentingRecipe> STREAM_CODEC = CDGRecipeParams.streamCodec(BulkFermentingRecipe::new, CDGProcessingRecipe::params);
    public static final RecipeSerializer<BulkFermentingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public BulkFermentingRecipe(CDGRecipeParams params) {
        super(params);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    public boolean apply(BulkFermenterBlockEntity be, boolean test) {
        Container items = be.itemHandler;
        if (items == null)
            return false;
        if (!(be.fluidCapability instanceof BulkFermenterBlockEntity.BulkFermenterFluidHandler fluids))
            return false;
        if (!getRequiredHeat().testBlazeBurner(be.highestHeatLevel))
            return false;
        if (be.getLevel() == null)
            return false;

        int slots = items.getContainerSize();
        int[] consumedFromSlot = new int[slots];
        for (SizedIngredient ingredient : getIngredients()) {
            boolean satisfied = false;
            for (int slot = 0; slot < slots; slot++) {
                ItemStack inSlot = items.getItem(slot);
                if (inSlot.isEmpty() || !ingredient.test(inSlot))
                    continue;
                if (inSlot.getCount() - consumedFromSlot[slot] < ingredient.getCount())
                    continue;
                consumedFromSlot[slot] += ingredient.getCount();
                satisfied = true;
                break;
            }
            if (!satisfied)
                return false;
        }

        int tankCount = fluids.size();
        int[] drainedFromTank = new int[tankCount];
        for (FluidIngredient ingredient : getFluidIngredients()) {
            int required = ingredient.amount();
            for (int tank = 0; tank < tankCount && required > 0; tank++) {
                FluidStack inTank = fluids.getStack(tank);
                if (inTank.isEmpty() || !ingredient.test(inTank))
                    continue;
                int available = inTank.getAmount() - drainedFromTank[tank];
                if (available <= 0)
                    continue;
                int taken = Math.min(required, available);
                drainedFromTank[tank] += taken;
                required -= taken;
            }
            if (required > 0)
                return false;
        }

        List<ItemStack> outputItems = new ArrayList<>();
        ProcessingOutput.rollOutput(be.getLevel().getRandom(), getRollableResults(), outputItems::add);

        List<FluidStack> outputFluids = new ArrayList<>();
        for (FluidStack result : getFluidResults())
            if (!result.isEmpty())
                outputFluids.add(result.copy());

        if (!fitsItems(items, consumedFromSlot, outputItems))
            return false;
        if (!fitsFluids(fluids, drainedFromTank, outputFluids))
            return false;

        if (test)
            return true;

        for (int slot = 0; slot < slots; slot++) {
            if (consumedFromSlot[slot] == 0)
                continue;
            CDGInv.extractItem(items, slot, consumedFromSlot[slot], false);
        }

        boolean fluidsAffected = false;
        for (int tank = 0; tank < tankCount; tank++) {
            if (drainedFromTank[tank] == 0)
                continue;
            FluidStack inTank = fluids.getStack(tank);
            FluidStack left = inTank.copy();
            left.setAmount(left.getAmount() - drainedFromTank[tank]);
            fluids.setStack(tank, left.getAmount() <= 0 ? FluidStack.EMPTY : left);
            fluidsAffected = true;
        }

        for (ItemStack output : outputItems)
            CDGInv.insertItemStacked(items, output.copy(), false);

        for (FluidStack output : outputFluids)
            fluids.insert(output.copy());

        if (fluidsAffected)
            be.onFluidStackChanged();

        return true;
    }

    private static boolean fitsItems(Container items, int[] consumedFromSlot, List<ItemStack> outputs) {
        if (outputs.isEmpty())
            return true;
        SimpleContainer scratch = new SimpleContainer(items.getContainerSize());
        for (int slot = 0; slot < items.getContainerSize(); slot++) {
            ItemStack inSlot = items.getItem(slot).copy();
            inSlot.shrink(consumedFromSlot[slot]);
            scratch.setItem(slot, inSlot);
        }
        for (ItemStack output : outputs)
            if (!CDGInv.insertItemStacked(scratch, output.copy(), false).isEmpty())
                return false;
        return true;
    }

    private static boolean fitsFluids(BulkFermenterBlockEntity.BulkFermenterFluidHandler fluids, int[] drainedFromTank, List<FluidStack> outputs) {
        if (outputs.isEmpty())
            return true;
        int tankCount = fluids.size();
        FluidStack[] scratch = new FluidStack[tankCount];
        for (int tank = 0; tank < tankCount; tank++) {
            FluidStack inTank = fluids.getStack(tank).copy();
            inTank.setAmount(Math.max(0, inTank.getAmount() - drainedFromTank[tank]));
            scratch[tank] = inTank.getAmount() == 0 ? FluidStack.EMPTY : inTank;
        }
        int capacity = fluids.getCapacity();
        for (FluidStack output : outputs) {
            int remaining = output.getAmount();
            for (int tank = 0; tank < tankCount && remaining > 0; tank++) {
                if (scratch[tank].isEmpty() || !FluidStack.areFluidsAndComponentsEqual(scratch[tank], output))
                    continue;
                int space = capacity - scratch[tank].getAmount();
                if (space <= 0)
                    continue;
                int placed = Math.min(space, remaining);
                scratch[tank].setAmount(scratch[tank].getAmount() + placed);
                remaining -= placed;
            }
            for (int tank = 0; tank < tankCount && remaining > 0; tank++) {
                if (!scratch[tank].isEmpty())
                    continue;
                int placed = Math.min(capacity, remaining);
                scratch[tank] = output.copyWithAmount(placed);
                remaining -= placed;
            }
            if (remaining > 0)
                return false;
        }
        return true;
    }

    @Override
    public RecipeSerializer<BulkFermentingRecipe> getSerializer() {
        return CDGRecipes.BULK_FERMENTING.getSerializer();
    }

    @Override
    public RecipeType<BulkFermentingRecipe> getType() {
        return CDGRecipes.BULK_FERMENTING.getType();
    }
}
