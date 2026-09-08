package com.jesz.createdieselgenerators.client.render;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermenterCTBehavior;
import com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineCTBehavior;
import com.jesz.createdieselgenerators.content.distillation.DistillationTankModel;
import com.jesz.createdieselgenerators.content.oil_barrel.OilBarrelCTBehavior;
import com.jesz.createdieselgenerators.content.sheetmetal.SheetMetalPanelModel;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CDGModels {
    private CDGModels() {
    }

    public static void register() {
        AllModels.register(CDGBlocks.MODULAR_DIESEL_ENGINE.get(), CTModel.of(new ModularDieselEngineCTBehavior()));
        AllModels.register(CDGBlocks.BULK_FERMENTER.get(), CTModel.of(new BulkFermenterCTBehavior()));
        AllModels.register(CDGBlocks.OIL_BARREL.get(), CTModel.of(new OilBarrelCTBehavior()));
        AllModels.register(CDGBlocks.DISTILLATION_TANK.get(), DistillationTankModel.of());
        AllModels.register(CDGBlocks.SHEET_METAL_PANEL.get(), SheetMetalPanelModel.of());

        MovementBehaviour head = MovementBehaviour.REGISTRY.get(CDGBlocks.PUMPJACK_HEAD.get());
        if (head != null)
            head.attachRender = new PumpjackHeadRenderBehaviour();
    }
}
