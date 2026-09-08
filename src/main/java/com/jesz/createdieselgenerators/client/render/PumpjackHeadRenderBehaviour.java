package com.jesz.createdieselgenerators.client.render;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.content.pumpjack.PumpjackBearingBlockEntity;
import com.jesz.createdieselgenerators.content.pumpjack.PumpjackHoleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.nbt.NBTHelper;
import com.zurrtum.create.client.api.behaviour.movement.MovementRenderBehaviour;
import com.zurrtum.create.client.api.behaviour.movement.MovementRenderState;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.zurrtum.create.content.contraptions.ControlledContraptionEntity;
import com.zurrtum.create.content.contraptions.bearing.BearingContraption;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PumpjackHeadRenderBehaviour implements MovementRenderBehaviour {
    @Override
    public @Nullable MovementRenderState getRenderState(Vec3 cameraPos, Font font, MovementContext context, VirtualRenderWorld renderWorld,
                                                       PoseStack.Pose modelPose, Matrix4f viewProjection) {
        BlockPos hole = NBTHelper.readBlockPos(context.data, "HolePos");
        if (!(context.world.getBlockEntity(hole) instanceof PumpjackHoleBlockEntity))
            return null;
        if (!(context.contraption instanceof BearingContraption bearingContraption))
            return null;
        if (!(context.world.getBlockEntity(context.contraption.anchor.relative(bearingContraption.getFacing().getOpposite())) instanceof PumpjackBearingBlockEntity))
            return null;

        float partialTicks = AnimationTickHolder.getPartialTicks();
        SuperByteBuffer cover = CachedBuffers.partial(CDGPartialModels.PUMPJACK_ROPE, context.state);
        Vec3 prevPos = context.position.subtract(context.motion);
        double contraptionAngle = ((ControlledContraptionEntity) context.contraption.entity).getAngle(partialTicks);
        double yDst = Mth.lerp(partialTicks, prevPos.y, context.position.y) - hole.getY() - 0.8f;

        if (bearingContraption.getFacing().getOpposite().getAxis() == Direction.Axis.X) {
            double zDst = Mth.lerp(partialTicks, prevPos.z, context.position.z) - hole.getZ() - 0.5f;
            float distanceFromHole = (float) Math.sqrt(zDst * zDst + yDst * yDst);
            double angle = -contraptionAngle - (180 * Math.atan2(yDst, zDst) / Math.PI) + 90;
            cover.transform(modelPose)
                    .translate(0.5, 0.5, 0.5)
                    .rotateXDegrees((float) angle)
                    .scale(1, distanceFromHole, 1);
        } else {
            double xDst = Mth.lerp(partialTicks, prevPos.x, context.position.x) - hole.getX() - 0.5;
            float distanceFromHole = (float) Math.sqrt(xDst * xDst + yDst * yDst);
            double angle = -contraptionAngle + (180 * Math.atan2(yDst, xDst) / Math.PI) - 90;
            cover.transform(modelPose)
                    .translate(0.5, 0.5, 0.5)
                    .rotateZDegrees((float) angle)
                    .scale(1, distanceFromHole, 1);
        }

        SuperByteBufferRenderState state = cover.useLevelLight(context.world, viewProjection).extractRenderState();
        return state::submit;
    }
}
