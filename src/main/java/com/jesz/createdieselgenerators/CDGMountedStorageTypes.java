package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.content.oil_barrel.OilBarrelMountedStorageType;
import com.jesz.createdieselgenerators.registrate.entry.RegistryEntry;
import com.zurrtum.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.zurrtum.create.api.registry.CreateRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class CDGMountedStorageTypes {

    public static final RegistryEntry<MountedFluidStorageType<?>, OilBarrelMountedStorageType> OIL_BARREL =
            simpleFluid("oil_barrel", OilBarrelMountedStorageType::new);

    private static <T extends MountedFluidStorageType<?>> RegistryEntry<MountedFluidStorageType<?>, T> simpleFluid(String name, Supplier<T> supplier) {
        Identifier id = CreateDieselGenerators.rl(name);
        return new RegistryEntry<>(id, Registry.register(CreateRegistries.MOUNTED_FLUID_STORAGE_TYPE, id, supplier.get()));
    }

    public static void register() {
    }
}
