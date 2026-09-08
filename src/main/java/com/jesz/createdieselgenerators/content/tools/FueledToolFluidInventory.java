package com.jesz.createdieselgenerators.content.tools;

import com.jesz.createdieselgenerators.CDGDataComponents;
import com.jesz.createdieselgenerators.content.fluids.SimpleFluidContent;
import com.zurrtum.create.infrastructure.fluids.FluidItemInventoryWrapper;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

public class FueledToolFluidInventory extends FluidItemInventoryWrapper {

    @Override
    public FluidStack getStack() {
        SimpleFluidContent content = stack.get(CDGDataComponents.FLUID_CONTENTS);
        return content == null ? FluidStack.EMPTY : content.copy();
    }

    @Override
    public void setStack(FluidStack fluid) {
        if (fluid.isEmpty())
            stack.remove(CDGDataComponents.FLUID_CONTENTS);
        else
            stack.set(CDGDataComponents.FLUID_CONTENTS, SimpleFluidContent.copyOf(fluid));
    }

    @Override
    public int getMaxAmountPerStack() {
        return stack.getItem() instanceof FueledToolItem tool ? tool.getCapacity(stack) : 0;
    }
}
