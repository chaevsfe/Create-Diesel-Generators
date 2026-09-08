package com.jesz.createdieselgenerators.content.canister;

import com.jesz.createdieselgenerators.CDGConfig;
import com.jesz.createdieselgenerators.content.tools.FueledToolItem;
import com.zurrtum.create.AllEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.jesz.createdieselgenerators.CDGFluids;

public class CanisterBlockItem extends BlockItem implements FueledToolItem {
    public CanisterBlockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, adder, tooltipFlag);
        List<Component> tooltipComponents = new java.util.ArrayList<>();
        createTooltip(tooltipComponents, stack);
        tooltipComponents.forEach(adder);
    }

    @Override
    public int getBaseCapacity(ItemStack stack) {
        return CDGFluids.mb(CDGConfig.common().CANISTER_CAPACITY.get());
    }

    @Override
    public int getCapacityEnchantmentAddition(ItemStack stack) {
        return CDGFluids.mb(CDGConfig.common().CANISTER_CAPACITY_ENCHANTMENT.get());
    }

    @Override
    public InteractionResult useOn(UseOnContext p_40581_) {
        return super.useOn(p_40581_);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCurrentFillLevel(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13 * (float) getCurrentFillLevel(stack) / getCapacity(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xEFEFEF;
    }
}
