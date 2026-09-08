package com.jesz.createdieselgenerators.mixins;

import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.content.tools.lighter.LighterItem;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void createdieselgenerators$tickLighter(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (!CDGItems.LIGHTER.isIn(entity.getItem()))
            return;
        CDGItems.LIGHTER.get().onEntityItemUpdate(entity.getItem(), entity);
    }
}
