package com.jesz.createdieselgenerators.content.pumpjack;

import com.mojang.math.Axis;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.content.contraptions.bearing.IBearingBlockEntity;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.visual.DynamicVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.instance.InstanceTypes;
import com.zurrtum.create.client.flywheel.lib.instance.OrientedInstance;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.zurrtum.create.client.flywheel.lib.visual.SimpleDynamicVisual;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class NoShaftBearingInstance<B extends KineticBlockEntity & IBearingBlockEntity> extends AbstractBlockEntityVisual<B> implements SimpleDynamicVisual {
    final OrientedInstance topInstance;

    final Axis rotationAxis;
    final Quaternionf blockOrientation;

    public NoShaftBearingInstance(VisualizationContext context, B blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        Direction facing = blockState.getValue(BlockStateProperties.FACING);
        rotationAxis = Axis.of(Direction.get(Direction.AxisDirection.POSITIVE, facing.getAxis()).step());

        blockOrientation = getBlockStateOrientation(facing);

        PartialModel top =
                blockEntity.isWoodenTop() ? AllPartialModels.BEARING_TOP_WOODEN : AllPartialModels.BEARING_TOP;

        topInstance = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(top))
                .createInstance();

        topInstance.position(getVisualPosition()).rotation(blockOrientation);
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        float interpolatedAngle = blockEntity.getInterpolatedAngle(ctx.partialTick());
        Quaternionf rot = rotationAxis.rotationDegrees(interpolatedAngle);

        rot.mul(blockOrientation);

        topInstance.rotation(rot)
                .setChanged();
    }

    @Override
    public void updateLight(float pt) {
        relight(pos, topInstance);
    }

    static Quaternionf getBlockStateOrientation(Direction facing) {
        Quaternionf orientation;

        if (facing.getAxis().isHorizontal()) {
            orientation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing.getOpposite()));
        } else {
            orientation = new Quaternionf();
        }

        orientation.mul(Axis.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(facing)));
        return orientation;
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

    }

    @Override
    protected void _delete() {
        topInstance.delete();
    }
}
