package com.jesz.createdieselgenerators.mixins;

import com.jesz.createdieselgenerators.content.oil_barrel.OilBarrelBlockEntity;
import com.zurrtum.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Shadow protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Inject(method = "getBlockEntityNBT", at=@At("RETURN"), remap = false)
    public void getBlockEntityNBT(Level world, BlockPos pos, CallbackInfoReturnable<CompoundTag> cir){
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof OilBarrelBlockEntity))
            return;
        CompoundTag nbt = cir.getReturnValue();
        if (nbt == null)
            return;
        nbt.read("Controller", BlockPos.CODEC).ifPresent(controller -> nbt.store("Controller", BlockPos.CODEC, toLocalPos(controller)));
    }
}
