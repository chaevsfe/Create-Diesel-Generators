package com.jesz.createdieselgenerators.client;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.CDGSpriteShifts;
import com.jesz.createdieselgenerators.client.model.CDGItemModels;
import com.jesz.createdieselgenerators.client.behaviour.CDGTooltipBehaviours;
import com.jesz.createdieselgenerators.client.gui.CDGScreens;
import com.jesz.createdieselgenerators.client.gui.CDGTooltipComponents;
import com.jesz.createdieselgenerators.client.render.CDGEntityRenders;
import com.jesz.createdieselgenerators.client.render.CDGFluidModels;
import com.jesz.createdieselgenerators.client.render.CDGModels;
import com.jesz.createdieselgenerators.client.valuebox.CDGValueBoxes;
import com.jesz.createdieselgenerators.ponder.CDGPonderPlugin;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import com.jesz.createdieselgenerators.client.model.CDGItemProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CDGClient {
    private CDGClient() {
    }

    public static void initClient() {
        CDGPartialModels.init();
        CDGSpriteShifts.init();
        CDGRegistrateClient.flush();
        CDGItemProperties.register();
        CDGItemModels.register();
        CDGModels.register();
        CDGValueBoxes.register();
        CDGTooltipBehaviours.register();
        CDGEntityRenders.register();
        CDGFluidModels.register();
        CDGScreens.register();
        CDGTooltipComponents.register();
        CDGItemTooltips.register();
        CDGClientEvents.register();
        PonderIndex.addPlugin(new CDGPonderPlugin());
    }
}
