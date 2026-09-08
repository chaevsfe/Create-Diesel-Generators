package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.registrate.entry.RegistryEntry;
import net.minecraft.sounds.SoundEvent;

import static com.jesz.createdieselgenerators.CreateDieselGenerators.REGISTRATE;

public class CDGSoundEvents {
    public static RegistryEntry<SoundEvent, SoundEvent> ENGINE_NORMAL;

    public static void register() {
        ENGINE_NORMAL = REGISTRATE.soundEvent("engine_normal");
    }
}
