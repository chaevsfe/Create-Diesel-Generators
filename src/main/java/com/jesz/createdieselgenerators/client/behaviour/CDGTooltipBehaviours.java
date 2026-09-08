package com.jesz.createdieselgenerators.client.behaviour;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.zurrtum.create.client.AllBlockEntityBehaviours;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CDGTooltipBehaviours {
    private CDGTooltipBehaviours() {
    }

    public static void register() {
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.BULK_FERMENTER.get(), CDGGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.BURNER.get(), CDGKineticGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.CANISTER.get(), CDGGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.CHEMICAL_TURRET.get(), CDGKineticGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.DIESEL_ENGINE.get(), CDGGeneratingKineticGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.DISTILLATION_TANK.get(), CDGGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.HUGE_DIESEL_ENGINE.get(), CDGGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.MODULAR_DIESEL_ENGINE.get(), CDGGeneratingKineticGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.OIL_BARREL.get(), CDGGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.PUMPJACK_HOLE.get(), CDGGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.POWERED_ENGINE_SHAFT.get(), CDGGeneratingKineticGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.PUMPJACK_CRANK.get(), CDGKineticGoggleBehaviour::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.ENCASED_GIRDER.get(), CDGKineticGoggleBehaviour::new);
    }
}
