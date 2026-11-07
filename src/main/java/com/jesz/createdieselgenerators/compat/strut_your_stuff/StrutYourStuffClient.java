package com.jesz.createdieselgenerators.compat.strut_your_stuff;

import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.cake.struts.compat.flywheel.StrutsFlywheelCompatLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class StrutYourStuffClient {
    private StrutYourStuffClient() {
    }

    public static void register() {
        ItemDescription.useKey(StrutYourStuffRegistryEntries.ANDESITE_GIRDER_STRUT.get(), "block.bits_n_bobs.girder_strut");
        StrutsFlywheelCompatLoader.registerStrutVisual(StrutYourStuffBlockEntityTypes.ANDESITE_GIRDER_STRUT.get());
    }
}
