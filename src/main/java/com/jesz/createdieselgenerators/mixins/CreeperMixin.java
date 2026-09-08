package com.jesz.createdieselgenerators.mixins;

import com.jesz.createdieselgenerators.CDGItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.jesz.createdieselgenerators.foundation.CDGInv;
import com.zurrtum.create.AllFluidItemInventory;
import com.jesz.createdieselgenerators.CDGFluids;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster {
    protected CreeperMixin(EntityType<? extends Monster> type, Level level) { super(type, level); }

    @Shadow public abstract void ignite();

    @Inject(method = "mobInteract", at = @At("TAIL"), remap = false)
    public void cdg$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack stackInHand = player.getItemInHand(hand);
        if(!stackInHand.is(CDGItems.LIGHTER.get()))
            return;
        if (!AllFluidItemInventory.has(stackInHand))
            return;
        FluidInventory fluid = AllFluidItemInventory.of(stackInHand);
        if (fluid.getStack(0).isEmpty())
            return;
        CDGInv.drain(fluid, CDGFluids.MB, false);
        ignite();
        this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);


    }
}
