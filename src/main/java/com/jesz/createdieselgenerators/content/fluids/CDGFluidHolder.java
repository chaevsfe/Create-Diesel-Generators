package com.jesz.createdieselgenerators.content.fluids;

import com.zurrtum.create.infrastructure.fluids.FluidEntry;
import net.minecraft.resources.Identifier;

public class CDGFluidHolder extends FluidEntry {
    public final CDGFluidProperties properties = new CDGFluidProperties();
    public final CDGFluidTypeProperties typeProperties = new CDGFluidTypeProperties();
    public Identifier stillTexture;
    public Identifier flowingTexture;
}
