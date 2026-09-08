package com.jesz.createdieselgenerators.client.model;

import com.zurrtum.create.client.AllModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CDGItemModels {
    private CDGItemModels() {
    }

    public static void register() {
        AllModels.register(ChemicalSprayerModel.ID, ChemicalSprayerModel.Unbaked.CODEC);
        AllModels.register(ChemicalSprayerModel.LIGHTER_ID, ChemicalSprayerModel.Unbaked.LIGHTER_CODEC);
        AllModels.register(HammerModel.ID, HammerModel.Unbaked.CODEC);
        AllModels.register(WireCuttersModel.ID, WireCuttersModel.Unbaked.CODEC);
        AllModels.register(MoldModel.ID, MoldModel.Unbaked.CODEC);
        AllModels.register(LighterItemModel.ID, LighterItemModel.Unbaked.CODEC);
    }
}
