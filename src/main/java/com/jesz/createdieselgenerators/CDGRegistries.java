package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.fuel_type.FuelType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class CDGRegistries {
    public static final ResourceKey<Registry<FuelType>> FUEL_TYPE = key("fuel_type");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(CreateDieselGenerators.rl(name));
    }

    public static void register() {
        DynamicRegistries.registerSynced(FUEL_TYPE, FuelType.CODEC, FuelType.NCODEC);
    }
}
