package com.jesz.createdieselgenerators.config;

import com.zurrtum.create.catnip.config.ConfigBase;

public class CDGClientConfig extends ConfigBase {
    public final ConfigGroup tooltips = group(0, "tooltips", "Tooltips");
    public final ConfigBool FUEL_TOOLTIPS = b(true, "fuel_tooltips", "Fuel type tooltip on buckets.");
    public final ConfigBool DIESEL_ENGINE_IN_JEI = b(true, "diesel_engine_in_recipe_viewer", "Whether Diesel Engines are shown in the recipe viewer.");
    public final ConfigBool ENGINES_EMIT_SOUND_ON_TRAINS = b(true, "engines_emit_sound_on_trains", "Diesel Engines emit sounds on trains.");

    @Override
    public String getName() {
        return "client";
    }
}
