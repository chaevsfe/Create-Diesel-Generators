package com.jesz.createdieselgenerators.client.behaviour;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.api.goggles.IHaveHoveringInformation;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CDGKineticGoggleBehaviour<T extends KineticBlockEntity> extends KineticTooltipBehaviour<T> {
    public CDGKineticGoggleBehaviour(T blockEntity) {
        super(blockEntity);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        int before = tooltip.size();
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (blockEntity instanceof IHaveGoggleInformation goggles)
            added |= goggles.addToGoggleTooltip(tooltip, isPlayerSneaking);
        return added && tooltip.size() > before;
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        int before = tooltip.size();
        boolean added = super.addToTooltip(tooltip, isPlayerSneaking);
        if (blockEntity instanceof IHaveHoveringInformation hovering)
            added |= hovering.addToTooltip(tooltip, isPlayerSneaking);
        return added && tooltip.size() > before;
    }
}
