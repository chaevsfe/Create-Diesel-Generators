package com.jesz.createdieselgenerators.compat.rei;

import dev.chaevsfe.createreiviewer.api.CreateReiApi;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;

public class CDGReiCommonPlugin implements REICommonPlugin {
    @Override
    public double getPriority() {
        return CreateReiApi.PLUGIN_PRIORITY;
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        if (!CDGReiSupport.available()) {
            return;
        }
        CDGReiDisplays.register(registry);
    }

    @Override
    public void postStage(PluginManager<REICommonPlugin> manager, ReloadStage stage) {
        if (!CDGReiSupport.available()) {
            return;
        }
        CDGReiDisplays.report(manager, stage);
    }
}
