package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.compat.strut_your_stuff.StrutYourStuffBlockEntityTypes;
import com.jesz.createdieselgenerators.compat.strut_your_stuff.StrutYourStuffRegistryEntries;
import com.jesz.createdieselgenerators.content.molds.MoldType;
import com.jesz.createdieselgenerators.events.GameEvents;
import com.jesz.createdieselgenerators.packets.CDGPackets;
import com.jesz.createdieselgenerators.registrate.CDGRegistrate;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.zurrtum.create.client.foundation.item.KineticStats;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import com.zurrtum.create.compat.Mods;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.world.entity.EntityTypes;

public class CreateDieselGenerators {
    public static final String ID = "createdieselgenerators";
    public static final Logger LOGGER = LoggerFactory.getLogger(CreateDieselGenerators.class);

    public static final CDGRegistrate REGISTRATE = CDGRegistrate.create(ID)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public static void registerBlocksEarly() {
        CDGDisplaySources.register();
        CDGBlocks.register();
        if (FabricLoader.getInstance().isModLoaded("struts"))
            StrutYourStuffRegistryEntries.register();
        com.jesz.createdieselgenerators.registrate.Registrate.registerFluidBlocks();
    }

    public static void registerFluidsEarly() {
        CDGFluids.register();
    }

    public static void init() {
        CDGConfig.register();

        com.jesz.createdieselgenerators.registrate.Registrate.registerFluidItems();
        CDGItems.register();
        CDGBlockEntityTypes.register();
        if (FabricLoader.getInstance().isModLoaded("struts"))
            StrutYourStuffBlockEntityTypes.register();
        CDGEntityTypes.register();
        CDGSoundEvents.register();
        CDGRecipes.register();
        CDGMenuTypes.register();
        MoldType.register();
        CDGMountedStorageTypes.register();
        CDGCreativeTab.register();
        CDGPackets.register();
        CDGDataComponents.register();
        CDGRegistries.register();

        GameEvents.register();
        com.jesz.createdieselgenerators.events.ModEvents.setup();
        REGISTRATE.register();
    }

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    public static Component lang(String path, Object... args) {
        return Component.translatable(ID + "." + path, args);
    }
}
