package com.jesz.createdieselgenerators.client.behaviour;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.api.goggles.IHaveHoveringInformation;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CDGGoggleBehaviour extends TooltipBehaviour<SmartBlockEntity> implements IHaveGoggleInformation, IHaveHoveringInformation {
    public CDGGoggleBehaviour(SmartBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return blockEntity instanceof IHaveGoggleInformation goggles && goggles.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return blockEntity instanceof IHaveHoveringInformation hovering && hovering.addToTooltip(tooltip, isPlayerSneaking);
    }
}
