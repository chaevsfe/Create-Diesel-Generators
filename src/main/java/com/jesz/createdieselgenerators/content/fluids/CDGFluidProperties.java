package com.jesz.createdieselgenerators.content.fluids;

public class CDGFluidProperties {
    public int levelDecreasePerBlock = 1;
    public int tickRate = 5;
    public int slopeFindDistance = 4;
    public float explosionResistance = 1.0F;

    public CDGFluidProperties levelDecreasePerBlock(int value) {
        this.levelDecreasePerBlock = value;
        return this;
    }

    public CDGFluidProperties tickRate(int value) {
        this.tickRate = value;
        return this;
    }

    public CDGFluidProperties slopeFindDistance(int value) {
        this.slopeFindDistance = value;
        return this;
    }

    public CDGFluidProperties explosionResistance(float value) {
        this.explosionResistance = value;
        return this;
    }
}
