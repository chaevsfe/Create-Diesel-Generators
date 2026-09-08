package com.jesz.createdieselgenerators.config;

import com.zurrtum.create.catnip.config.ConfigBase;

public class CDGServerConfig extends ConfigBase {
    public final ConfigGroup engines = group(0, "diesel_engines", "Diesel Engines");
    public final ConfigFloat TURBOCHARGED_ENGINE_MULTIPLIER = f(2.0f, 0.0f, Float.MAX_VALUE, "turbocharged_engine_speed_multiplier", "Turbocharged Diesel Engine speed multiplier.");
    public final ConfigFloat TURBOCHARGED_ENGINE_BURN_RATE_MULTIPLIER = f(1.0f, 0.0f, Float.MAX_VALUE, "turbocharged_engine_burn_rate_multiplier", "Turbocharged Diesel Engine burn rate multiplier.");
    public final ConfigBool NORMAL_ENGINES = b(true, "normal_diesel_engines", "Whether Normal Diesel Engines are enabled.");
    public final ConfigBool MODULAR_ENGINES = b(true, "modular_diesel_engines", "Whether Modular Diesel Engines are enabled.");
    public final ConfigBool HUGE_ENGINES = b(true, "huge_diesel_engines", "Whether Huge Diesel Engines are enabled.");
    public final ConfigBool ENGINES_FILLED_WITH_ITEMS = b(false, "engines_filled_with_a_bucket", "Whether Diesel Engines can be filled with an item.");
    public final ConfigBool ENGINES_DISABLED_WITH_REDSTONE = b(true, "engines_disabled_with_redstone", "Whether Diesel Engines can be disabled with redstone.");
    public final ConfigBool ANALOG_SPEED_CONTROL = b(true, "engines_controlled_by_analog_lever", "Whether Diesel Engines can be controlled with an analog lever.");

    public final ConfigGroup oil = group(0, "oil", "Oil");
    public final ConfigInt OIL_CHUNK_INFINITE_THRESHOLD = i(10_000_000, 0, Integer.MAX_VALUE, "infinite_oil_chunk_threshold", "Infinite oil chunk threshold.");
    public final ConfigInt OIL_CHUNK_THRESHOLD = i(4_000_000, 0, Integer.MAX_VALUE, "oil_chunk_threshold", "Oil chunk threshold.");
    public final ConfigBool DISABLE_NORMAL_OIL_CHUNKS = b(false, "disable_normal_oil_chunks", "Disable normal oil chunks.");
    public final ConfigBool DISABLE_HIGH_OIL_CHUNKS = b(false, "disable_high_oil_chunks", "Disable high oil chunks.");
    public final ConfigFloat OIL_MULTIPLIER = f(1.3f, 0.0f, Float.MAX_VALUE, "normal_oil_chunk_multiplier", "Normal oil chunk oil amount multiplier.");
    public final ConfigFloat HIGH_OIL_MULTIPLIER = f(2.0f, 0.0f, Float.MAX_VALUE, "high_oil_chunk_multiplier", "High oil chunk oil amount multiplier.");
    public final ConfigFloat OIL_CHUNK_SCALE = f(1.0f, 0.0f, Float.MAX_VALUE, "oil_chunk_map_scale", "Oil chunk map scale.");
    public final ConfigInt MAX_OIL_SCANNER_LEVEL = i(10000, 0, Integer.MAX_VALUE, "max_oil_scanner_level", "Max Oil Scanner level.");

    public final ConfigGroup machines = group(0, "machines", "Machines");
    public final ConfigInt MAX_OIL_BARREL_WIDTH = i(3, 1, 16, "max_oil_barrel_width", "Maximum width of Oil Barrels.");
    public final ConfigInt MAX_OIL_BARREL_LENGTH_PER_WIDTH = i(4, 1, 16, "max_oil_barrel_length_per_width", "Maximum Oil Barrel length for each unit of width.");
    public final ConfigBool CANISTER_SPOUT_FILLING = b(true, "canister_can_be_filled_by_spouts", "Whether Canisters can be filled by Spouts.");
    public final ConfigBool COMBUSTIBLES_BLOW_UP = b(true, "combustibles_blow_up", "Whether combustible fluids explode when set on fire.");
    public final ConfigInt DISTILLATION_MIN_HEIGHT = i(3, 2, 7, "distillation_tower_minimum_height", "Minimum height of the Distillation Tower required to process recipes.");

    @Override
    public String getName() {
        return "server";
    }
}
