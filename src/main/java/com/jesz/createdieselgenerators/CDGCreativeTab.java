package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.compat.strut_your_stuff.StrutYourStuffRegistryEntries;
import com.jesz.createdieselgenerators.content.molds.MoldType;
import com.jesz.createdieselgenerators.content.track_layers_bag.TrackLayersBagItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceKey;

public class CDGCreativeTab {

    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateDieselGenerators.rl("cdg_creative_tab"));

    public static final CreativeModeTab CREATIVE_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            CREATIVE_TAB_KEY,
            FabricCreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cdg_creative_tab"))
                    .icon(() -> CDGBlocks.DIESEL_ENGINE.asStack())
                    .displayItems((pParameters, output) -> {
                        output.accept(CDGItems.ENGINE_PISTON.get());
                        output.accept(CDGItems.WIRE_CUTTERS.get());
                        output.accept(CDGItems.HAMMER.get());
                        output.accept(CDGItems.ENGINE_SILENCER.get());
                        output.accept(CDGItems.ENGINE_TURBO.get());
                        output.accept(CDGBlocks.DIESEL_ENGINE.get());
                        output.accept(CDGBlocks.MODULAR_DIESEL_ENGINE.get());
                        output.accept(CDGBlocks.HUGE_DIESEL_ENGINE.get());
                        output.accept(CDGItems.DISTILLATION_CONTROLLER.get());
                        output.accept(CDGItems.OIL_SCANNER.get());
                        output.accept(CDGBlocks.PUMPJACK_HOLE.get());
                        output.accept(CDGBlocks.PUMPJACK_BEARING.get());
                        output.accept(CDGBlocks.PUMPJACK_CRANK.get());
                        output.accept(CDGBlocks.PUMPJACK_HEAD.get());
                        output.accept(CDGItems.WOOD_CHIPS.get());
                        output.accept(CDGBlocks.CHIP_WOOD_BEAM.get());
                        output.accept(CDGBlocks.CHIP_WOOD_BLOCK.get());
                        output.accept(CDGBlocks.CHIP_WOOD_STAIRS.get());
                        output.accept(CDGBlocks.CHIP_WOOD_SLAB.get());
                        output.accept(CDGBlocks.CANISTER.get());
                        output.accept(CDGBlocks.OIL_BARREL.get());
                        output.accept(CDGBlocks.BASIN_LID.get());
                        output.accept(CDGBlocks.ASPHALT_BLOCK.get());
                        output.accept(CDGBlocks.ASPHALT_STAIRS.get());
                        output.accept(CDGBlocks.ASPHALT_SLAB.get());
                        output.accept(CDGBlocks.BULK_FERMENTER.get());
                        output.accept(CDGBlocks.ANDESITE_GIRDER.get());
                        if (FabricLoader.getInstance().isModLoaded("struts"))
                            StrutYourStuffRegistryEntries.fillCreativeTab(output);
                        output.accept(CDGBlocks.BURNER.get());
                        output.accept(CDGBlocks.CHEMICAL_TURRET.get());
                        output.accept(CDGBlocks.SHEET_METAL_PANEL.get());
                        output.accept(CDGFluids.CRUDE_OIL.getBucket().get());
                        output.accept(CDGFluids.BIODIESEL.getBucket().get());
                        output.accept(CDGFluids.DIESEL.getBucket().get());
                        output.accept(CDGFluids.GASOLINE.getBucket().get());
                        output.accept(CDGFluids.PLANT_OIL.getBucket().get());
                        output.accept(CDGFluids.ETHANOL.getBucket().get());
                        output.accept(CDGItems.KELP_HANDLE.get());
                        output.accept(CDGItems.LIGHTER.get());
                        output.accept(CDGItems.CHEMICAL_SPRAYER.get());
                        output.accept(CDGItems.CHEMICAL_SPRAYER_LIGHTER.get());
                        output.accept(CDGItems.TRACK_LAYERS_BAG.get());
                        output.accept(TrackLayersBagItem.full());
                        
                        output.accept(CDGItems.ENTITY_FILTER.get());
                        MoldType.types.forEach(mt -> {
                            ItemStack moldStack = CDGItems.MOLD.asStack();
                            moldStack.set(CDGDataComponents.MOLD_TYPE, mt.getId());
                            output.accept(moldStack);
                        });
                        for (var fluid : CDGFluids.CONCRETE)
                            output.accept(fluid.getBucket().get());
                    })
                    .build());

    public static void register() {
    }

}

