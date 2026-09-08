package com.jesz.createdieselgenerators.content.tools;

import com.jesz.createdieselgenerators.CDGConfig;
import com.jesz.createdieselgenerators.CDGDataComponents;
import com.zurrtum.create.AllEnchantments;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import java.util.List;
import com.jesz.createdieselgenerators.content.fluids.SimpleFluidContent;
import com.jesz.createdieselgenerators.CDGFluids;

public interface FueledToolItem {

    default int getBaseCapacity(ItemStack stack){
        return CDGFluids.mb(CDGConfig.common().TOOL_CAPACITY.get());
    }

    default int getCapacityEnchantmentAddition(ItemStack stack){
        return CDGFluids.mb(CDGConfig.common().TOOL_CAPACITY_ENCHANTMENT.get());
    }

    default int getCapacity(ItemStack stack){
        int enchantmentLevel = 0;
        if (stack.has(DataComponents.ENCHANTMENTS))
            for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : stack.get(DataComponents.ENCHANTMENTS).entrySet())
                if (enchantment.getKey().is(AllEnchantments.CAPACITY))
                    enchantmentLevel = enchantment.getIntValue();

        return getBaseCapacity(stack) + (getCapacityEnchantmentAddition(stack) * enchantmentLevel);
    }

    default FluidStack readFluid(ItemStack stack){
        return stack.has(CDGDataComponents.FLUID_CONTENTS) ? stack.get(CDGDataComponents.FLUID_CONTENTS).copy() : FluidStack.EMPTY;
    }

    default void writeFluid(ItemStack stack, FluidStack fluid){
        stack.set(CDGDataComponents.FLUID_CONTENTS, SimpleFluidContent.copyOf(fluid));
    }

    default int getCurrentFillLevel(ItemStack stack){
        return readFluid(stack).getAmount();
    }

    default void createTooltip(List<Component> tooltip, ItemStack stack){
        if(stack.has(CDGDataComponents.FLUID_CONTENTS)) {
            FluidStack fluid = readFluid(stack);
            if(fluid.isEmpty()){
                tooltip.add(Component.translatable("createdieselgenerators.tooltip.empty").withStyle(ChatFormatting.GRAY));
                return;
            }
            tooltip.add(CreateLang.fluidName(fluid).component()
                    .withStyle(ChatFormatting.GRAY)
                    .append(" ")
                    .append(CreateLang.number(fluid.getAmount() / (double) CDGFluids.MB).style(ChatFormatting.GOLD).component())
                    .append(Component.translatable("create.generic.unit.millibuckets").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(" / "))
                    .append(CreateLang.number(getCapacity(stack) / (double) CDGFluids.MB).style(ChatFormatting.GRAY).component())
                    .append(Component.translatable("create.generic.unit.millibuckets").withStyle(ChatFormatting.GRAY)));
            return;
        }
        tooltip.add(Component.translatable("createdieselgenerators.tooltip.empty").withStyle(ChatFormatting.GRAY));
    }
}
