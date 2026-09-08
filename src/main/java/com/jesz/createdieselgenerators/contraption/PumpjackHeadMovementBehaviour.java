package com.jesz.createdieselgenerators.contraption;

import com.jesz.createdieselgenerators.content.pumpjack.PumpjackBearingBBlock;
import com.jesz.createdieselgenerators.content.pumpjack.PumpjackBearingBlockEntity;
import com.jesz.createdieselgenerators.content.pumpjack.PumpjackHoleBlockEntity;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.content.contraptions.bearing.BearingContraption;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import com.zurrtum.create.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class PumpjackHeadMovementBehaviour extends MovementBehaviour {
    @Nullable
    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public boolean isActive(MovementContext context) {
        if (!(context.contraption instanceof BearingContraption))
            return false;
        if (((BearingContraption) context.contraption).getFacing().getAxis() == Direction.Axis.Y || context.state.getValue(PumpjackBearingBBlock.FACING).getAxis() != ((BearingContraption) context.contraption).getFacing().getClockWise().getAxis())
            return false;
        return context.world.getBlockEntity(context.contraption.anchor.relative(((BearingContraption) context.contraption).getFacing().getOpposite())) instanceof PumpjackBearingBlockEntity;
    }

    BlockPos holePos;
    BlockPos headPos;
    @Override
    public void tick(MovementContext context) {
        super.tick(context);
        PumpjackBearingBlockEntity bearing = null;
        if (context.world.getBlockEntity(context.contraption.anchor.relative(((BearingContraption) context.contraption).getFacing().getOpposite())) instanceof PumpjackBearingBlockEntity be)
            bearing = be;
        if (bearing == null)
            return;
        headPos = new BlockPos(
                context.contraption.anchor.getX() + context.localPos.getX(),
                context.contraption.anchor.getY() + context.localPos.getY(),
                context.contraption.anchor.getZ() + context.localPos.getZ());
        holePos = headPos;
        for (int i = 0; i < 32; i++) {
            if (context.world.getBlockEntity(holePos) instanceof PumpjackHoleBlockEntity phbe)
                break;
            else
                holePos = holePos.below();
        }

        if (context.world.getBlockEntity(holePos) instanceof PumpjackHoleBlockEntity holeBE && bearing.crankSpeed >= 8) {
            holeBE.headPos = bearing.getBlockState().getValue(FACING).getAxis() == Direction.Axis.X ? context.localPos.getZ() : context.localPos.getX();
            holeBE.bearingPos = bearing.getBlockState().getValue(FACING).getAxis() == Direction.Axis.X ? bearing.bearingBPos.getZ() : bearing.bearingBPos.getX();
            if ((bearing.crankAngle + 270) % 360 < (context.data.getFloatOr("OldCrankAngle", 0f) + 270) % 360)
                holeBE.pumpjackRotation(bearing.isLarge);
        }
        context.data.putFloat("OldCrankAngle", bearing.crankAngle);

        context.data.store("HolePos", BlockPos.CODEC, holePos);
    }
}
