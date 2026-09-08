package com.jesz.createdieselgenerators.client.valuebox;

import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;

import static com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlock.FACING;

@Environment(EnvType.CLIENT)
public class HugeDieselEngineValueBox extends ValueBoxTransform.Sided {

    @Override
    protected boolean isSideActive(BlockState state, Direction side) {
        return state.getValue(FACING).getAxis() != side.getAxis();
    }


    @Override
    public Vec3 getLocalOffset(BlockState state) {
        Vec3 location = new Vec3(0.5, 0.5, 1);
        location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
        location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);
        return location;
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }
}
