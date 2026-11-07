package com.jesz.createdieselgenerators.mixins;

import com.jesz.createdieselgenerators.datafix.V4540;
import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public class DataFixersMixin {
    @Inject(
            method = "addFixers(Lcom/mojang/datafixers/DataFixerBuilder;Lnet/minecraft/util/filefix/FileFixerUpper$Builder;)V",
            at = @At(value = "CONSTANT", args = "intValue=4541")
    )
    private static void createdieselgenerators$addSchema(DataFixerBuilder builder, FileFixerUpper.Builder fileFixer, CallbackInfo ci) {
        V4540.register(builder);
    }
}
