package com.jesz.createdieselgenerators.client.model;

import com.jesz.createdieselgenerators.CDGDataComponents;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.track_layers_bag.TrackLayersBagItem;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class CDGItemProperties {
    public static final Identifier OIL_SCANNER_STATE = CreateDieselGenerators.rl("oil_scanner_state");
    public static final Identifier TRACKS = CreateDieselGenerators.rl("tracks");

    private CDGItemProperties() {
    }

    public static void register() {
        RangeSelectItemModelProperties.ID_MAPPER.put(OIL_SCANNER_STATE, OilScannerState.MAP_CODEC);
        RangeSelectItemModelProperties.ID_MAPPER.put(TRACKS, Tracks.MAP_CODEC);
    }

    public record OilScannerState() implements RangeSelectItemModelProperty {
        public static final MapCodec<OilScannerState> MAP_CODEC = MapCodec.unit(new OilScannerState());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            return stack.getOrDefault(CDGDataComponents.OIL_SCANNER_STATE, 0);
        }

        @Override
        public MapCodec<OilScannerState> type() {
            return MAP_CODEC;
        }
    }

    public record Tracks() implements RangeSelectItemModelProperty {
        public static final MapCodec<Tracks> MAP_CODEC = MapCodec.unit(new Tracks());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            return TrackLayersBagItem.getTracks(stack).getCount();
        }

        @Override
        public MapCodec<Tracks> type() {
            return MAP_CODEC;
        }
    }
}
