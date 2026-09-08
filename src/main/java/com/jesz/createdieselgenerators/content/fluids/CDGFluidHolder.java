package com.jesz.createdieselgenerators.content.fluids;

import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import com.zurrtum.create.infrastructure.fluids.FluidBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BucketItem;

public class CDGFluidHolder {
    public final CDGFluidProperties properties = new CDGFluidProperties();
    public final CDGFluidTypeProperties typeProperties = new CDGFluidTypeProperties();
    public Identifier stillTexture;
    public Identifier flowingTexture;
    public FlowableFluid still;
    public FlowableFluid flowing;
    public FluidBlock block;
    public BucketItem bucket;
}
