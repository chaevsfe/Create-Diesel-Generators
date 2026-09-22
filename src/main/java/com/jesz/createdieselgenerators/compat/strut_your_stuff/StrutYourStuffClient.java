package com.jesz.createdieselgenerators.compat.strut_your_stuff;

import com.cake.struts.compat.flywheel.StrutsFlywheelCompatLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class StrutYourStuffClient {
    private StrutYourStuffClient() {
    }

    public static void register() {
        StrutsFlywheelCompatLoader.registerStrutVisual(StrutYourStuffBlockEntityTypes.ANDESITE_GIRDER_STRUT.get());
    }
}
