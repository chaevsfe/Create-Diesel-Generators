package com.jesz.createdieselgenerators;

import net.fabricmc.api.ModInitializer;

public final class CreateDieselGeneratorsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateDieselGeneratorsPlugin.verifyEarlyRegistrationComplete();
        CreateDieselGenerators.init();
    }
}
