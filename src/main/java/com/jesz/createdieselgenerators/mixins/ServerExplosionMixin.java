package com.jesz.createdieselgenerators.mixins;

import com.jesz.createdieselgenerators.events.GameEvents;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {
    @Inject(method = "explode", at = @At("RETURN"))
    private void createdieselgenerators$igniteCombustibles(CallbackInfoReturnable<Integer> cir) {
        ServerExplosion explosion = (ServerExplosion) (Object) this;
        Vec3 center = explosion.center();
        GameEvents.onExplosion(explosion.level(), center.x, center.y, center.z);
    }
}
