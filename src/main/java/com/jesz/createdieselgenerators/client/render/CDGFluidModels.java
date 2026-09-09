package com.jesz.createdieselgenerators.client.render;

import com.jesz.createdieselgenerators.CDGFluids;
import com.jesz.createdieselgenerators.content.fluids.CDGFluidHolder;
import com.jesz.createdieselgenerators.registrate.entry.FluidEntry;
import com.zurrtum.create.client.AllFluidConfigs;
import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;

@Environment(EnvType.CLIENT)
public final class CDGFluidModels {
    private CDGFluidModels() {
    }

    public static void register() {
        register(CDGFluids.PLANT_OIL);
        register(CDGFluids.CRUDE_OIL);
        register(CDGFluids.BIODIESEL);
        register(CDGFluids.DIESEL);
        register(CDGFluids.GASOLINE);
        register(CDGFluids.ETHANOL);
        for (FluidEntry<FlowableFluid> concrete : CDGFluids.CONCRETE)
            register(concrete);
    }

    private static void register(FluidEntry<FlowableFluid> entry) {
        CDGFluidHolder holder = entry.getHolder();
        FluidModel.Unbaked model = new FluidModel.Unbaked(new Material(holder.stillTexture), new Material(holder.flowingTexture), null, null);
        AllFluidConfigs.MODEL.put(holder.still, model);
    }
}
