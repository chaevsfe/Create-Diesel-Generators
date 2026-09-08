package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.client.CDGClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CreateDieselGeneratorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CDGConfig.registerClient();
        CDGClient.initClient();
    }
}
