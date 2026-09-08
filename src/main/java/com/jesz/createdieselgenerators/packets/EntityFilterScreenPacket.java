package com.jesz.createdieselgenerators.packets;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.entity_filter.EntityAttribute;
import com.jesz.createdieselgenerators.content.entity_filter.EntityFilterMenu;
import com.zurrtum.create.infrastructure.component.AttributeFilterWhitelistMode;
import com.zurrtum.create.infrastructure.packet.c2s.FilterScreenPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record EntityFilterScreenPacket(FilterScreenPacket.Option option, EntityAttribute attribute) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EntityFilterScreenPacket> TYPE =
        new CustomPacketPayload.Type<>(CreateDieselGenerators.rl("entity_filter_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityFilterScreenPacket> STREAM_CODEC = StreamCodec.composite(
        FilterScreenPacket.Option.STREAM_CODEC, EntityFilterScreenPacket::option,
        EntityAttribute.STREAM_CODEC, EntityFilterScreenPacket::attribute,
        EntityFilterScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer player) {
        if (player == null)
            return;

        if (player.containerMenu instanceof EntityFilterMenu menu) {
            if (option == FilterScreenPacket.Option.WHITELIST)
                menu.whitelistMode = AttributeFilterWhitelistMode.WHITELIST_DISJ;
            if (option == FilterScreenPacket.Option.WHITELIST2)
                menu.whitelistMode = AttributeFilterWhitelistMode.WHITELIST_CONJ;
            if (option == FilterScreenPacket.Option.BLACKLIST)
                menu.whitelistMode = AttributeFilterWhitelistMode.BLACKLIST;

            if (attribute != null && (option == FilterScreenPacket.Option.ADD_TAG || option == FilterScreenPacket.Option.ADD_INVERTED_TAG))
                menu.appendSelectedAttribute(attribute, option == FilterScreenPacket.Option.ADD_INVERTED_TAG);
        }
    }
}
