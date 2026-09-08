package com.jesz.createdieselgenerators.content.fluids;

public class CDGFluidTypeProperties {
    public int viscosity = 1000;
    public int density = 1000;
    public int temperature = 300;
    public int lightLevel = 0;
    public boolean canSwim = true;
    public boolean canDrown = true;

    public CDGFluidTypeProperties viscosity(int value) {
        this.viscosity = value;
        return this;
    }

    public CDGFluidTypeProperties density(int value) {
        this.density = value;
        return this;
    }

    public CDGFluidTypeProperties temperature(int value) {
        this.temperature = value;
        return this;
    }

    public CDGFluidTypeProperties lightLevel(int value) {
        this.lightLevel = value;
        return this;
    }

    public CDGFluidTypeProperties canSwim(boolean value) {
        this.canSwim = value;
        return this;
    }

    public CDGFluidTypeProperties canDrown(boolean value) {
        this.canDrown = value;
        return this;
    }
}
