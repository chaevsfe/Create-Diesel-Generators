package com.jesz.createdieselgenerators.content.diesel_engine;

import com.jesz.createdieselgenerators.CDGConfig;

import java.util.function.Supplier;

public enum EngineTypes {
    NORMAL(() -> CDGConfig.server().NORMAL_ENGINES.get()), MODULAR(() -> CDGConfig.server().MODULAR_ENGINES.get()), HUGE(() -> CDGConfig.server().HUGE_ENGINES.get());

    final Supplier<Boolean> isEnabled;

    EngineTypes(Supplier<Boolean> isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean enabled() {
        return isEnabled.get();
    }
}
