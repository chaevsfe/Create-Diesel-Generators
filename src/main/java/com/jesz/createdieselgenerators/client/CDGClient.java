package com.jesz.createdieselgenerators.client;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.foundation.CDGAnimationTicks;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.zurrtum.create.client.foundation.item.KineticStats;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.CDGSpriteShifts;
import com.jesz.createdieselgenerators.client.model.CDGItemModels;
import com.jesz.createdieselgenerators.compat.strut_your_stuff.StrutYourStuffClient;
import com.jesz.createdieselgenerators.client.behaviour.CDGTooltipBehaviours;
import com.jesz.createdieselgenerators.client.gui.CDGScreens;
import com.jesz.createdieselgenerators.client.gui.CDGTooltipComponents;
import com.jesz.createdieselgenerators.client.gui.render.CDGGuiRenderers;
import com.jesz.createdieselgenerators.client.render.CDGEntityRenders;
import com.jesz.createdieselgenerators.client.render.CDGFluidModels;
import com.jesz.createdieselgenerators.client.render.CDGModels;
import com.jesz.createdieselgenerators.client.valuebox.CDGValueBoxes;
import com.jesz.createdieselgenerators.ponder.CDGPonderPlugin;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import com.jesz.createdieselgenerators.client.model.CDGItemProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public final class CDGClient {
    private CDGClient() {
    }

    public static void initClient() {
        CreateDieselGenerators.REGISTRATE.setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
        CDGAnimationTicks.bind(AnimationTickHolder::getTicks);
        CDGPartialModels.init();
        CDGSpriteShifts.init();
        CDGRegistrateClient.flush();
        if (FabricLoader.getInstance().isModLoaded("struts"))
            StrutYourStuffClient.register();
        CDGItemProperties.register();
        CDGItemModels.register();
        CDGModels.register();
        CDGValueBoxes.register();
        CDGTooltipBehaviours.register();
        CDGEntityRenders.register();
        CDGFluidModels.register();
        CDGScreens.register();
        CDGTooltipComponents.register();
        CDGGuiRenderers.register();
        CDGItemTooltips.register();
        CDGClientEvents.register();
        PonderIndex.addPlugin(new CDGPonderPlugin());
    }
}
