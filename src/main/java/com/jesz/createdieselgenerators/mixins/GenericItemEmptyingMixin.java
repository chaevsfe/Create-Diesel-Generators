package com.jesz.createdieselgenerators.mixins;

import com.jesz.createdieselgenerators.content.tools.FueledToolFluidInventory;
import com.jesz.createdieselgenerators.content.tools.FueledToolItem;
import com.zurrtum.create.catnip.data.Pair;
import com.zurrtum.create.content.fluids.transfer.GenericItemEmptying;
import com.zurrtum.create.foundation.fluid.FluidHelper;
import com.zurrtum.create.infrastructure.fluids.BucketFluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidItemInventory;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GenericItemEmptying.class)
public class GenericItemEmptyingMixin {
    @Inject(method = "emptyItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"), remap = false, cancellable = true)
    private static void createdieselgenerators$emptyFueledTool(BlockAndLightGetter level, ItemStack stack, boolean simulate, CallbackInfoReturnable<Pair<FluidStack, ItemStack>> cir) {
        if (!(stack.getItem() instanceof FueledToolItem))
            return;
        try (FluidItemInventory inventory = FluidHelper.getFluidInventory(stack.copyWithCount(1))) {
            if (!(inventory instanceof FueledToolFluidInventory tool))
                return;
            FluidStack drained = tool.removeStackWithAmount(BucketFluidInventory.CAPACITY);
            if (drained.isEmpty())
                return;
            if (!simulate)
                stack.shrink(1);
            cir.setReturnValue(Pair.of(drained, tool.getContainer()));
        }
    }
}
