package com.jesz.createdieselgenerators.packets;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CDGPackets {

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(EntityFilterScreenPacket.TYPE, EntityFilterScreenPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(EntityFilterScreenPacket.TYPE,
            (payload, context) -> context.player().level().getServer().execute(() -> payload.handle(context.player())));
    }
}
