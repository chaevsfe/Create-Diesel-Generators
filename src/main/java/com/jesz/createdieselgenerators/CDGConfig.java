package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.config.CDGClientConfig;
import com.jesz.createdieselgenerators.config.CDGCommonConfig;
import com.jesz.createdieselgenerators.config.CDGServerConfig;
import com.zurrtum.create.catnip.config.Builder;
import com.zurrtum.create.catnip.config.ConfigBase;

public class CDGConfig {
    private static CDGServerConfig server;
    private static CDGCommonConfig common;
    private static CDGClientConfig client;

    public static CDGServerConfig server() {
        return server;
    }

    public static CDGCommonConfig common() {
        return common;
    }

    public static CDGClientConfig client() {
        return client;
    }

    public static void register() {
        common = Builder.create(CDGCommonConfig::new, CreateDieselGenerators.ID, "common", true);
        server = Builder.create(CDGServerConfig::new, CreateDieselGenerators.ID, "server", true);
    }

    public static void registerClient() {
        client = Builder.create(CDGClientConfig::new, CreateDieselGenerators.ID, "client", true);
    }
}
