package com.jesz.createdieselgenerators.contraption;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import com.zurrtum.create.content.trains.entity.CarriageContraption;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


public class DieselEngineMovementBehaviour extends MovementBehaviour {
    @Override
    public boolean isActive(MovementContext context) {
        return context.contraption instanceof CarriageContraption && super.isActive(context);
    }

    @Nullable
    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return CDGBlocks.DIESEL_ENGINE.asStack();
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world != null && context.world.isClientSide())
            com.jesz.createdieselgenerators.client.sound.CDGSounds.tickContraptionEngine(context);
    }
}
