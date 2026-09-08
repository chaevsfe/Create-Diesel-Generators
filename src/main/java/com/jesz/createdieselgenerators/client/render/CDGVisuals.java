package com.jesz.createdieselgenerators.client.render;

import com.jesz.createdieselgenerators.registrate.fn.BlockEntityVisualFactory;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CDGVisuals {
    private CDGVisuals() {
    }

    public static <T extends KineticBlockEntity> BlockEntityVisualFactory<T> singleAxisRotating(PartialModel model) {
        SimpleBlockEntityVisualizer.Factory<T> factory = SingleAxisRotatingVisual.of(model);
        return factory::create;
    }
}
