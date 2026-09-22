package com.jesz.createdieselgenerators.compat.strut_your_stuff;

import com.cake.struts.content.StrutModelType;
import com.cake.struts.content.StrutRenderLayer;
import com.cake.struts.content.block.StrutBlockItem;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.registrate.entry.BlockEntry;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.jesz.createdieselgenerators.CreateDieselGenerators.REGISTRATE;

public class StrutYourStuffRegistryEntries {
    public static final StrutModelType ANDESITE_GIRDER_MODEL =
            new StrutModelType(CreateDieselGenerators.rl("block/girder_strut/andesite_girder"),
                    CreateDieselGenerators.rl("block/andesite_girder_strut_end"), StrutRenderLayer.CUTOUT);

    public static final BlockEntry<TrussGirderStrutBlock> ANDESITE_GIRDER_STRUT = REGISTRATE.block("andesite_girder_strut",
                    props -> new TrussGirderStrutBlock(props, ANDESITE_GIRDER_MODEL))
            .properties(p -> p.strength(3f, 6f))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .onRegisterAfter(
                    Registries.ITEM,
                    v -> ItemDescription.useKey(v, "block.bits_n_bobs.girder_strut")
            )
            .item(StrutBlockItem::new)
            .build()
            .register();

    public static void register() {
    }

    public static void fillCreativeTab(CreativeModeTab.Output output) {
        output.accept(ANDESITE_GIRDER_STRUT.get());
    }
}
