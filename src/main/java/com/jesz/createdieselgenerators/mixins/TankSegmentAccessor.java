package com.jesz.createdieselgenerators.mixins;

import com.zurrtum.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmartFluidTankBehaviour.TankSegment.class)
public interface TankSegmentAccessor {
    @Accessor("capacity")
    void createdieselgenerators$setCapacity(int capacity);
}
