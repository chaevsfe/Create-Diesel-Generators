package com.jesz.createdieselgenerators.client;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGConfig;
import com.jesz.createdieselgenerators.CDGRegistries;
import com.jesz.createdieselgenerators.content.diesel_engine.EngineTypes;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.catnip.lang.Lang;
import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.foundation.item.TooltipHelper;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.content.equipment.goggles.GogglesItem;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.infrastructure.config.CKinetics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class CDGItemTooltips {
    private CDGItemTooltips() {
    }

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, tooltip) -> addToItemTooltip(stack, tooltip));
    }

    private static void addToItemTooltip(ItemStack stack, List<Component> tooltip) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null)
            return;
        if (!com.zurrtum.create.client.infrastructure.config.AllConfigs.client().tooltips.get())
            return;

        Item item = stack.getItem();
        if (item instanceof BucketItem bucket && CDGConfig.client().FUEL_TOOLTIPS.get())
            addFuelStats(bucket, tooltip, minecraft);

        if (!(item instanceof BlockItem blockItem) || !IRotate.StressImpact.isEnabled())
            return;
        net.minecraft.world.level.block.Block block = blockItem.getBlock();
        if (block != CDGBlocks.DIESEL_ENGINE.get() && block != CDGBlocks.MODULAR_DIESEL_ENGINE.get()
                && block != CDGBlocks.HUGE_DIESEL_ENGINE.get())
            return;
        addEngineCapacity(block, tooltip, minecraft);
    }

    private static void addFuelStats(BucketItem bucket, List<Component> tooltip, Minecraft minecraft) {
        Fluid fluid = bucket.content;
        FuelType type = FuelType.getTypeFor(minecraft.level.registryAccess().lookupOrThrow(CDGRegistries.FUEL_TYPE), fluid);
        if (type.normal().speed() == 0)
            return;

        if (!minecraft.hasAltDown()) {
            tooltip.add(1, Component.translatable("createdieselgenerators.tooltip.holdForFuelStats",
                    Component.translatable("createdieselgenerators.tooltip.keyAlt").withStyle(ChatFormatting.GRAY))
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        tooltip.add(1, Component.translatable("createdieselgenerators.tooltip.holdForFuelStats",
                Component.translatable("createdieselgenerators.tooltip.keyAlt").withStyle(ChatFormatting.WHITE))
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(2, Component.empty());

        List<EngineTypes> enabledEngines = Arrays.stream(EngineTypes.values()).filter(EngineTypes::enabled).toList();
        if (enabledEngines.isEmpty())
            return;
        int enginesEnabled = enabledEngines.size();
        EngineTypes currentEngine = enabledEngines.get((AnimationTickHolder.getTicks() % 120) / 20 % enginesEnabled);
        FuelType.PerEngineProperties generated = type.getGenerated(currentEngine);

        int line = 3;
        if (enginesEnabled != 1)
            tooltip.add(line++, Component.translatable("block.createdieselgenerators."
                    + (currentEngine == EngineTypes.MODULAR ? "large_" : currentEngine == EngineTypes.HUGE ? "huge_" : "")
                    + "diesel_engine").withStyle(ChatFormatting.GRAY));
        tooltip.add(line++, Component.translatable("createdieselgenerators.tooltip.fuelSpeed",
                CreateLang.number(generated.speed()).component().withStyle(FontHelper.Palette.STANDARD_CREATE.primary()))
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(line++, Component.translatable("createdieselgenerators.tooltip.fuelStress",
                CreateLang.number(generated.strength()).component().withStyle(FontHelper.Palette.STANDARD_CREATE.primary()))
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(line++, Component.translatable("createdieselgenerators.tooltip.fuelBurnRate",
                CreateLang.number(generated.burn() * 20).component().withStyle(FontHelper.Palette.STANDARD_CREATE.primary()))
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(line++, Component.empty());
        tooltip.add(line++, Component.translatable("createdieselgenerators.tooltip.burnerStrength",
                CreateLang.number(type.burnerStrength() * 100).text(" %").component()
                        .withStyle(FontHelper.Palette.STANDARD_CREATE.primary()))
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(line, Component.empty());
    }

    private static void addEngineCapacity(net.minecraft.world.level.block.Block block, List<Component> tooltip, Minecraft minecraft) {
        CKinetics config = com.zurrtum.create.infrastructure.config.AllConfigs.server().kinetics;
        int highestCapacity = 0;
        int highestStressCapacity = 0;

        for (var entry : minecraft.level.registryAccess().lookupOrThrow(CDGRegistries.FUEL_TYPE).listElements().toList()) {
            FuelType type = entry.value();
            FuelType.PerEngineProperties properties = block == CDGBlocks.DIESEL_ENGINE.get() ? type.normal()
                    : block == CDGBlocks.MODULAR_DIESEL_ENGINE.get() ? type.modular() : type.huge();
            if (properties.speed() == 0)
                continue;
            highestCapacity = (int) Math.max(highestCapacity, properties.strength() / properties.speed());
            highestStressCapacity = (int) Math.max(highestStressCapacity, properties.strength());
        }

        tooltip.add(Component.empty());
        CreateLang.translate("tooltip.capacityProvided").style(ChatFormatting.GRAY).addTo(tooltip);

        IRotate.StressImpact impact = highestCapacity >= config.highCapacity.get() ? IRotate.StressImpact.HIGH
                : highestCapacity >= config.mediumCapacity.get() ? IRotate.StressImpact.MEDIUM : IRotate.StressImpact.LOW;
        IRotate.StressImpact opposite = IRotate.StressImpact.values()[IRotate.StressImpact.values().length - 2 - impact.ordinal()];
        LangBuilder builder = CreateLang.builder()
                .add(CreateLang.text(TooltipHelper.makeProgressBar(3, impact.ordinal() + 1)).style(opposite.getAbsoluteColor()));

        if (GogglesItem.isWearingGoggles(minecraft.player)) {
            builder.add(CreateLang.number(highestCapacity)).text("x ").add(CreateLang.translate("generic.unit.rpm")).addTo(tooltip);
            LangBuilder amount = CreateLang.number(highestStressCapacity).add(CreateLang.translate("generic.unit.stress"));
            CreateLang.text(" -> ").add(CreateLang.translate("tooltip.up_to", amount)).style(ChatFormatting.DARK_GRAY).addTo(tooltip);
            return;
        }
        builder.translate("tooltip.capacityProvided." + Lang.asId(impact.name())).addTo(tooltip);
    }
}
