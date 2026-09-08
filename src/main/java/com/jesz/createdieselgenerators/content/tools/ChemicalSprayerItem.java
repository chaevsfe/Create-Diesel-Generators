package com.jesz.createdieselgenerators.content.tools;

import com.jesz.createdieselgenerators.CDGRegistries;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import com.zurrtum.create.AllEnchantments;
import com.zurrtum.create.AllSoundEvents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import java.util.List;
import java.util.Random;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.jesz.createdieselgenerators.CDGFluids;

public class ChemicalSprayerItem extends Item implements FueledToolItem {
    boolean lighter;
    public ChemicalSprayerItem(Properties properties, boolean lighter) {
        super(properties);
        this.lighter = lighter;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, adder, tooltipFlag);
        List<Component> tooltipComponents = new java.util.ArrayList<>();
        createTooltip(tooltipComponents, stack);
        tooltipComponents.forEach(adder);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xEFEFEF;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if(getCurrentFillLevel(stackInHand) > 0)
            player.startUsingItem(hand);
        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 1000;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCurrentFillLevel(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13 * (getCurrentFillLevel(stack) / (float) getCapacity(stack)));
    }


    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
        FluidStack fluidStack = readFluid(stack);
        if (!fluidStack.isEmpty() && !level.isClientSide()) {
            if (count % 2 == 0) {
                boolean fire = FuelType.getTypeFor(level.registryAccess().lookupOrThrow(CDGRegistries.FUEL_TYPE), fluidStack.getFluid()).normal().speed() != 0;
                ChemicalSprayerProjectileEntity projectile = ChemicalSprayerProjectileEntity.spray(level, fluidStack, (fire && lighter) || fluidStack.getFluid().isSame(Fluids.LAVA), fluidStack.getFluid().isSame(Fluids.WATER));
                projectile.setPos(player.position().add(0, 1.5f, 0));
                projectile.shootFromRotation(player, player.getXRot() + new Random().nextFloat(-5, 5), player.getYRot() + new Random().nextFloat(-5, 5), 0.0f, 1.0f, 1.0f);
                level.addFreshEntity(projectile);
                fluidStack.setAmount(Math.max(0, fluidStack.getAmount() - CDGFluids.MB));
            }
            if (!(player instanceof Player p && p.isCreative()) && count % 25 == 0)
                writeFluid(stack, fluidStack);
        } else if (!fluidStack.isEmpty() && count % 2 == 0)
            AllSoundEvents.MIXING.playAt(level, player.blockPosition(), .75f, 1, true);
        super.onUseTick(level, player, stack, count);
    }
}
