package com.jesz.createdieselgenerators.content.pumpjack;

import com.zurrtum.create.content.redstone.displayLink.DisplayLinkContext;
import com.zurrtum.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.zurrtum.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PumpjackOilAmountDisplaySource extends SingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof PumpjackHoleBlockEntity be))
            return EMPTY_LINE;

        return Component.literal(be.oilAmount / 1000 + "B");
    }

    @Override
    public boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}