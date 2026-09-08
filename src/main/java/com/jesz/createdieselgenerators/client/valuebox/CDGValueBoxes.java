package com.jesz.createdieselgenerators.client.valuebox;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.client.behaviour.ChemicalTurretValueBox;
import com.jesz.createdieselgenerators.content.pumpjack.PumpjackCrankBlockEntity;
import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.RotationDirectionScrollBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.client.catnip.lang.Lang;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CDGValueBoxes {
    private CDGValueBoxes() {
    }

    public static void register() {
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.DIESEL_ENGINE.get(),
                be -> new RotationDirectionScrollBehaviour(be, CreateLang.translateDirect("contraptions.windmill.rotation_direction"), new DieselEngineValueBox()));
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.MODULAR_DIESEL_ENGINE.get(),
                be -> new RotationDirectionScrollBehaviour(be, CreateLang.translateDirect("contraptions.windmill.rotation_direction"), new ModularDieselEngineValueBox()));
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.HUGE_DIESEL_ENGINE.get(),
                be -> new RotationDirectionScrollBehaviour(be, CreateLang.translateDirect("contraptions.windmill.rotation_direction"), new HugeDieselEngineValueBox()));
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.PUMPJACK_CRANK.get(), CrankSizeScroll::new);
        AllBlockEntityBehaviours.add(CDGBlockEntityTypes.CHEMICAL_TURRET.get(),
                be -> new FilteringBehaviour<>(be, new ChemicalTurretValueBox()));
    }

    public enum CrankSizeIcon implements INamedIconOptions {
        NORMAL(AllIcons.I_CLEAR),
        LARGE(AllIcons.I_PLACE);

        private final AllIcons icon;

        CrankSizeIcon(AllIcons icon) {
            this.icon = icon;
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return "createdieselgenerators.tooltip.crank." + Lang.asId(name());
        }
    }

    public static class CrankSizeScroll extends ScrollOptionBehaviour<PumpjackCrankBlockEntity.CrankSize> {
        public CrankSizeScroll(PumpjackCrankBlockEntity be) {
            super(CrankSizeIcon.class, size -> CrankSizeIcon.values()[size.ordinal()],
                    net.minecraft.network.chat.Component.translatable("createdieselgenerators.pumpjack_crank.crank_size"), be, new PumpjackCrankValueBox());
        }
    }
}
