package com.jesz.createdieselgenerators.content.fluids;

import com.zurrtum.create.foundation.fluid.FluidTank;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import java.util.function.Consumer;

public class CDGFluidTank extends FluidTank {
    private final Consumer<FluidStack> onChange;

    public CDGFluidTank(int capacity) {
        this(capacity, null);
    }

    public CDGFluidTank(int capacity, Consumer<FluidStack> onChange) {
        super(capacity);
        this.onChange = onChange;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFluidAmount() {
        return getFluid().getAmount();
    }

    public int getSpace() {
        return capacity - getFluidAmount();
    }

    @Override
    public void setStack(int slot, FluidStack stack) {
        super.setStack(slot, stack);
        onContentsChanged();
    }

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        onContentsChanged();
    }

    @Override
    public void markDirty() {
        onContentsChanged();
    }

    protected void onContentsChanged() {
        if (onChange != null)
            onChange.accept(getFluid());
    }
}
