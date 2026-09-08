package com.jesz.createdieselgenerators;

import com.zurrtum.create.api.registry.CreateRegisterPlugin;

public final class CreateDieselGeneratorsPlugin implements CreateRegisterPlugin {
    private static boolean blocksRegistered;
    private static boolean fluidsRegistered;

    @Override
    public void onBlockRegister() {
        if (blocksRegistered)
            throw new IllegalStateException("Create Fly invoked Diesel Generators block registration more than once");
        CreateDieselGenerators.registerBlocksEarly();
        blocksRegistered = true;
    }

    @Override
    public void onFluidRegister() {
        if (fluidsRegistered)
            throw new IllegalStateException("Create Fly invoked Diesel Generators fluid registration more than once");
        CreateDieselGenerators.registerFluidsEarly();
        fluidsRegistered = true;
    }

    public static void verifyEarlyRegistrationComplete() {
        if (!blocksRegistered || !fluidsRegistered)
            throw new IllegalStateException("Create Fly did not invoke Diesel Generators early registration (blocks="
                    + blocksRegistered + ", fluids=" + fluidsRegistered + ")");
    }
}
